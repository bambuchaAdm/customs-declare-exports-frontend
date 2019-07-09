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

package typedcaching.persistance

import typedcaching.model.ExportsCacheModel

object DeclarationStore {

  var store: Map[String, ExportsCacheModel] = {
    println("New STORE...")
    Map.empty
  }

  def findInCache(sessionId: String): Either[String, ExportsCacheModel] = store.get(sessionId) match {
    case Some(foundItem) => Right(foundItem)
    case None => Left("Not found")
  }

  def storeInCache(ecm: ExportsCacheModel) = {
    store = store + (ecm.sessionId -> ecm)
    println(s"STORE: $store")
    ecm
  }
}

