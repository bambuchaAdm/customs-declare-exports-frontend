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

import java.time.LocalDateTime

import base.CustomExportsBaseSpec
import forms.Choice.AllowedChoiceValues.SupplementaryDec
import forms.Ducr
import forms.declaration.ConsignmentReferences
import forms.declaration.ConsignmentReferencesSpec._
import helpers.views.declaration.{CommonMessages, ConsignmentReferencesMessages}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify}
import play.api.libs.json.{JsObject, JsString, JsValue}
import play.api.test.Helpers._
import services.cache.ExportsCacheModel

class ConsignmentReferencesControllerSpec
    extends CustomExportsBaseSpec with ConsignmentReferencesMessages with CommonMessages {

  private val uri = uriWithContextPath("/declaration/consignment-references")

  import ConsignmentReferencesControllerSpec._

  override def beforeEach() {
    super.beforeEach()
    authorizedUser()
    withNewCaching(createModelWithNoItems(SupplementaryDec))
    withCaching[ConsignmentReferences](None, ConsignmentReferences.id)
  }

  override def afterEach() {
    super.afterEach()
    reset(mockCustomsCacheService, mockExportsCacheService)
  }

  "Consignment References Controller on GET" should {

    "return 200 code" in {

      val result = route(app, getRequest(uri)).get
      status(result) must be(OK)
      verifyTheCacheIsUnchanged()
    }

    "not populate the form fields if cache is empty" in {

      val result = route(app, getRequest(uri)).get
      val resultAsString = contentAsString(result)

      resultAsString.replaceAll(" ", "") must include("name=\"ducr.ducr\"\nvalue=\"\"")
      resultAsString.replaceAll(" ", "") must include("name=\"lrn\"\nvalue=\"\"")
      verifyTheCacheIsUnchanged()
    }

    "populate the form fields with data from cache" in {

      val cachedData = ExportsCacheModel(
        "SessionId",
        "DraftId",
        LocalDateTime.now(),
        LocalDateTime.now(),
        "SMP",
        consignmentReferences = Some((correctConsignmentReferences))
      )
      withNewCaching(cachedData)

      val result = route(app, getRequest(uri)).get
      val resultAsString = contentAsString(result)

      resultAsString.replaceAll(" ", "") must include("name=\"ducr.ducr\"\nvalue=\"" + exemplaryDucr + "\"")
      resultAsString.replaceAll(" ", "") must include(
        "name=\"lrn\"\nvalue=\"" + correctConsignmentReferences.lrn + "\""
      )
    }
  }

  "Consignment References Controller on POST" should {

    "proceed when no UCR provided by user" in {

      val validForm = buildConsignmentReferencesTestData(lrn = "123ABC")
      val result = route(app, postRequest(uri, validForm)).get

      contentAsString(result) mustNot include(messages("error.ducr"))
    }

    "save data to the cache" in {

      route(app, postRequest(uri, correctConsignmentReferencesJSON)).get.map { _ =>
        verify(mockCustomsCacheService)
          .cache[ConsignmentReferences](any(), ArgumentMatchers.eq(ConsignmentReferences.id), any())(
            any(),
            any(),
            any()
          )
        verify(mockExportsCacheService).update(any(), any())
        verify(mockExportsCacheService).get(any())
      }
    }

    "return 303 code" in {

      val result = route(app, postRequest(uri, correctConsignmentReferencesJSON)).get

      status(result) must be(SEE_OTHER)
    }

    "redirect to 'Exporter Details' page" in {

      val result = route(app, postRequest(uri, correctConsignmentReferencesJSON)).get
      val header = result.futureValue.header

      header.headers.get("Location") must be(Some("/customs-declare-exports/declaration/exporter-details"))
      theCacheModelUpdated.consignmentReferences.get.ducr must be(Some(Ducr("8GB123456789012-1234567890QWERTYUIO")))
    }
  }
}

object ConsignmentReferencesControllerSpec {
  def buildConsignmentReferencesTestData(ducr: String = "", lrn: String = ""): JsValue = JsObject(
    Map("ducr.ducr" -> JsString(ducr), "lrn" -> JsString(lrn))
  )
}
