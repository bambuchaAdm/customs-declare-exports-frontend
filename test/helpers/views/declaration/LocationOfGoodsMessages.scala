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

package helpers.views.declaration

trait LocationOfGoodsMessages {

  val locationOfGoods: String = "supplementary.goodsLocation"

  val title: String = locationOfGoods + ".title"
  val typeOfLocation: String = locationOfGoods + ".typeOfLocation"
  val typeOfLocationError: String = typeOfLocation + ".error"
  val typeOfLocationEmpty: String = typeOfLocation + ".empty"
  val qualifierOfIdent: String = locationOfGoods + ".qualifierOfIdentification"
  val qualifierOfIdentError: String = qualifierOfIdent + ".error"
  val qualifierOfIdentEmpty: String = qualifierOfIdent + ".empty"
  val identOfLocation: String = locationOfGoods + ".identificationOfLocation"
  val identOfLocationError: String = identOfLocation + ".error"
  val additionalQualifier: String = locationOfGoods + ".additionalQualifier"
  val additionalQualifierError: String = additionalQualifier + ".error"
  val locationAddress: String = locationOfGoods + ".addressLine"
  val locationAddressError: String = locationAddress + ".error"
  val logPostCode: String = locationOfGoods + ".postCode"
  val logPostCodeError: String = logPostCode + ".error"
  val city: String = locationOfGoods + ".city"
  val cityError: String = city + ".error"
}
