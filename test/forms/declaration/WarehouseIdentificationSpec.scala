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

package forms.declaration

import forms.declaration.TransportCodes._
import play.api.libs.json.{JsObject, JsString, JsValue}

object WarehouseIdentificationSpec {
  private val warehouseTypeCode = "R"
  private val warehouseId = "1234567GB"
  private val office = "Office"
  private val inlandModeOfTransportCode = Maritime

  val correctWarehouseIdentification =
    WarehouseIdentification(Some(office), Some(warehouseTypeCode), Some(warehouseId), Some(inlandModeOfTransportCode))
  val emptyWarehouseIdentification = WarehouseIdentification(None, None, None, None)
  val correctWarehouseIdentificationJSON: JsValue =
    JsObject(
      Map(
        "supervisingCustomsOffice" -> JsString("12345678"),
        "identificationType" -> JsString(warehouseTypeCode),
        "identificationNumber" -> JsString(warehouseId),
        "inlandModeOfTransportCode" -> JsString(Rail)
      )
    )
  val emptyWarehouseIdentificationJSON: JsValue =
    JsObject(
      Map(
        "supervisingCustomsOffice" -> JsString(""),
        "identificationType" -> JsString(""),
        "identificationNumber" -> JsString(""),
        "inlandModeOfTransportCode" -> JsString("")
      )
    )
}
