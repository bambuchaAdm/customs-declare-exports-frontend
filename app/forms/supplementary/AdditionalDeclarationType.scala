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

package forms.supplementary

import play.api.data.Forms.text
import play.api.data.{Form, Forms, Mapping}
import play.api.libs.json.Json

case class AdditionalDeclarationType(
  value: String // 1 upper case alphabetic character
)

object AdditionalDeclarationType {
  implicit val format = Json.format[AdditionalDeclarationType]

  private val allowedValues =
    Set(AllowedAdditionalDeclarationTypes.Simplified, AllowedAdditionalDeclarationTypes.Standard)

  val formMapping: Mapping[AdditionalDeclarationType] = Forms.single(
    "additionalDeclarationType" -> text(maxLength = 1)
      .verifying(
        "supplementary.declarationType.inputText.errorMessage",
        input => input.nonEmpty && allowedValues(input)
      )
      .transform[AdditionalDeclarationType](
        value => AdditionalDeclarationType(value),
        additionalDeclarationType => additionalDeclarationType.value
      )
  )

  val formId = "AdditionalDeclarationType"

  def form(): Form[AdditionalDeclarationType] = Form(formMapping)

  object AllowedAdditionalDeclarationTypes {
    val Simplified = "Y"
    val Standard = "Z"
  }
}
