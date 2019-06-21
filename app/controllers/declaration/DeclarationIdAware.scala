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

package controllers.declaration

import services.DeclarationIDStore
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.SessionId

import cats.instances.future._
import cats.data.OptionT

import scala.concurrent.{ExecutionContext, Future}

case class DeclarationId(sessionId: SessionId, declarationId: String)

//use action refiner ??
trait DeclarationIdAware {
  val store: DeclarationIDStore

  def declarationId(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[DeclarationId]] = {
    val f = for {
      sessionId <- OptionT.fromOption(hc.sessionId)
      decId <- OptionT(store.get(sessionId))
    } yield decId
    
    f.value
  }
}
