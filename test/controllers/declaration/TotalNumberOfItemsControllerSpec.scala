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

import base.CustomExportsBaseSpec
import forms.Choice.AllowedChoiceValues.SupplementaryDec
import forms.declaration.TotalNumberOfItems
import helpers.views.declaration.TotalNumberOfItemsMessages
import org.mockito.Mockito.reset
import play.api.libs.json.{JsObject, JsString, JsValue}
import play.api.test.Helpers._

class TotalNumberOfItemsControllerSpec extends CustomExportsBaseSpec with TotalNumberOfItemsMessages {

  private val uri = uriWithContextPath("/declaration/total-numbers-of-items")

  override def beforeEach {
    super.beforeEach()
    authorizedUser()
    withCaching[TotalNumberOfItems](None)
    withNewCaching(createModelWithNoItems(SupplementaryDec))
  }

  override def afterEach() {
    super.afterEach()
    reset(mockCustomsCacheService, mockExportsCacheService)
  }

  "Total Number Of Items Controller on GET" should {

    "return 200 code" in {

      val Some(result) = route(app, getRequest(uri))

      status(result) must be(OK)
    }

    "read item from cache and display it" in {

      val cachedData = TotalNumberOfItems(Some("7987.1"), Some("1.33"), " 631.1")
      withNewCaching(createModelWithNoItems(SupplementaryDec).copy(totalNumberOfItems = Some(cachedData)))

      val Some(result) = route(app, getRequest(uri))
      val page = contentAsString(result)

      status(result) must be(OK)
      page must include("7987.1")
      page must include("1.33")
      page must include("631.1")
    }
  }

  "Total Number Of Items Controller on POST" should {

    "validate request and redirect - correct values for all fields (integers)" in {

      val allFields: JsValue =
        JsObject(
          Map(
            "totalAmountInvoiced" -> JsString("456"),
            "exchangeRate" -> JsString("789"),
            "totalPackage" -> JsString("123")
          )
        )
      val Some(result) = route(app, postRequest(uri, allFields))

      status(result) must be(SEE_OTHER)
      redirectLocation(result) must be(Some("/customs-declare-exports/declaration/nature-of-transaction"))
      theCacheModelUpdated.totalNumberOfItems.get mustBe TotalNumberOfItems(
        totalAmountInvoiced = Some("456"),
        exchangeRate = Some("789"),
        totalPackage = "123"
      )
    }

    "validate request and redirect - correct values for all fields (decimals)" in {

      val allFields: JsValue =
        JsObject(
          Map(
            "totalAmountInvoiced" -> JsString("456.78"),
            "exchangeRate" -> JsString("789.789"),
            "totalPackage" -> JsString("123")
          )
        )
      val Some(result) = route(app, postRequest(uri, allFields))

      status(result) must be(SEE_OTHER)

      redirectLocation(result) must be(Some("/customs-declare-exports/declaration/nature-of-transaction"))
      theCacheModelUpdated.totalNumberOfItems.get mustBe TotalNumberOfItems(
        totalAmountInvoiced = Some("456.78"),
        exchangeRate = Some("789.789"),
        totalPackage = "123"
      )
    }

    "validate request and redirect - all inputs alphabetic" in {

      val allFields: JsValue =
        JsObject(
          Map(
            "itemsQuantity" -> JsString("test"),
            "totalAmountInvoiced" -> JsString("test"),
            "exchangeRate" -> JsString("test"),
            "totalPackage" -> JsString("test")
          )
        )
      val Some(result) = route(app, postRequest(uri, allFields))

      status(result) must be(BAD_REQUEST)

      contentAsString(result) must include(messages(totalAmountInvoicedError))
      contentAsString(result) must include(messages(exchangeRateError))
      contentAsString(result) must include(messages(totalPackageQuantityError))
      verifyTheCacheIsUnchanged()
    }

    "validate request and redirect - all inputs too long" in {

      val allFields: JsValue =
        JsObject(
          Map(
            "totalAmountInvoiced" -> JsString("12312312312312123"),
            "exchangeRate" -> JsString("1212121231123123"),
            "totalPackage" -> JsString("123456789")
          )
        )
      val Some(result) = route(app, postRequest(uri, allFields))

      status(result) must be(BAD_REQUEST)

      contentAsString(result) must include(messages(totalAmountInvoicedError))
      contentAsString(result) must include(messages(exchangeRateError))
      contentAsString(result) must include(messages(totalPackageQuantityError))
      verifyTheCacheIsUnchanged()
    }

    "validate request and redirect - Total Amount Invoiced / Exchange Rate too long decimal format" in {

      val allFields: JsValue = JsObject(
        Map(
          "totalAmountInvoiced" -> JsString("12312312312312.122"),
          "exchangeRate" -> JsString("1212121.123456"),
          "totalPackage" -> JsString("123")
        )
      )

      val Some(result) = route(app, postRequest(uri, allFields))
      status(result) must be(BAD_REQUEST)

      contentAsString(result) must include(messages(totalAmountInvoicedError))
      contentAsString(result) must include(messages(exchangeRateError))
      verifyTheCacheIsUnchanged()
    }

    "validate request and redirect - Total Amount Invoiced / Exchange Rate too long base integer" in {

      val allFields: JsValue = JsObject(
        Map(
          "totalAmountInvoiced" -> JsString("12312312312312123.12"),
          "exchangeRate" -> JsString("1212121231.12345"),
          "totalPackage" -> JsString("123")
        )
      )

      val Some(result) = route(app, postRequest(uri, allFields))
      status(result) must be(BAD_REQUEST)

      contentAsString(result) must include(messages(totalAmountInvoicedError))
      contentAsString(result) must include(messages(exchangeRateError))
      verifyTheCacheIsUnchanged()
    }
  }
}
