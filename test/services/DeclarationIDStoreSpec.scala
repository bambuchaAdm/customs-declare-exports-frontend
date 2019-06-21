/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import base.DeclarationIDGenerator
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.SessionId

import scala.concurrent.ExecutionContext.Implicits.global

class DeclarationIDStoreSpec extends WordSpec with Matchers with ScalaFutures {

  val store = new DeclarationIDStore

  "Declaration ID" should {
    
    "be saved and retrieved from the DB using the session ID" in {
      val sessionId = SessionId("my-session-id")
      implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(sessionId))
      val declarationId = new DeclarationIDGenerator().generateId.getOrElse(
        throw new IllegalStateException("Something went wrong generating the Declaration ID")
      )
      
      store.save(declarationId)
      
      store.get(sessionId).futureValue shouldBe Some(declarationId)
    }
  }
}
