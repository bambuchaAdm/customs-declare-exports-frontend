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

package typedcaching.model

import java.util.UUID

import forms.declaration.DispatchLocation
import play.api.libs.json.Json
import typedcaching.persistance.DeclarationStore.{findInCache, storeInCache}

case class ExportsCacheModel(
  sessionId: String,
  draftId: String = UUID.randomUUID().toString,
  dispatchLocation: Option[DispatchLocation] = None
)

object ExportsCacheModel {
  implicit val format = Json.format[ExportsCacheModel]

  implicit object CacheableDeclaration extends Cacheable[ExportsCacheModel] {

    def save(id: String, model: ExportsCacheModel): ExportsCacheModel = {
      storeInCache(model)
      model
    }

    def find(id: String): Either[String, ExportsCacheModel] = findInCache(id)
  }
}
