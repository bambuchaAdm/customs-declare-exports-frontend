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

package models.declaration.dectype

import forms.declaration.DispatchLocation
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType
import models.declaration.SummaryContainer
import services.cache.ExportsCacheModel

case class DeclarationTypeSupplementary(
  dispatchLocation: Option[DispatchLocation],
  additionalDeclarationType: Option[AdditionalDeclarationType]
) extends SummaryContainer {

  override def isEmpty: Boolean = dispatchLocation.isEmpty && additionalDeclarationType.isEmpty
}

object DeclarationTypeSupplementary {
  val id = "DeclarationType"

  def apply(cacheData: ExportsCacheModel): DeclarationTypeSupplementary = DeclarationTypeSupplementary(
    dispatchLocation = cacheData.dispatchLocation,
    additionalDeclarationType = cacheData.additionalDeclarationType
  )
}
