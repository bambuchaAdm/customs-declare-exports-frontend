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

package controllers

import base.CustomExportsBaseSpec
import forms.Choice
import forms.Choice._
import helpers.views.declaration.ChoiceMessages
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Cookie, Result}
import play.api.test.Helpers._

class ChoiceControllerSpec extends CustomExportsBaseSpec with ChoiceMessages {

  private val choiceUri = uriWithContextPath("/choice")

  before {
    authorizedUser()
    withCaching[Choice](None, Choice.choiceId)
  }

  after {
    reset(mockCustomsCacheService)
    reset(mockDeclarationIDGenerator)
  }

  "Choice Controller on GET" should {

    "return 200 status code" in {
      val Some(result) = route(app, getRequest(choiceUri))

      status(result) mustBe OK
    }

    "read item from cache and display it" in {

      val cachedData = Choice("SMP")
      withCaching[Choice](Some(cachedData), Choice.choiceId)

      val Some(result) = route(app, getRequest(choiceUri))
      val stringResult = contentAsString(result)

      status(result) mustBe OK
      stringResult must include("""value="SMP" checked="checked"""")
    }
  }

  "ChoiceController on POST" should {

    "display the choice page with error" when {

      "no value provided for choice" in {

        val emptyForm = JsObject(Map("" -> JsString("")))
        val Some(result) = route(app, postRequest(choiceUri, emptyForm))

        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include(messages(choiceEmpty))
      }

      "wrong value provided for choice" in {

        val wrongForm = JsObject(Map("value" -> JsString("test")))
        val Some(result) = route(app, postRequest(choiceUri, wrongForm))

        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include(messages(choiceError))
      }
    }

    "save the choice data to the cache" in {

      val validChoiceForm = JsObject(Map("value" -> JsString("SMP")))
      val Some(result) = route(app, postRequest(choiceUri, validChoiceForm))
      await(result)

      verify(mockCustomsCacheService).cache[Choice](any(), ArgumentMatchers.eq(Choice.choiceId), any())(any(), any(), any())
    }

    "redirect to dispatch location page when 'Supplementary declaration' is selected" in {

      val correctForm = JsObject(Map("value" -> JsString(AllowedChoiceValues.SupplementaryDec)))
      val Some(result) = route(app, postRequest(choiceUri, correctForm))

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/customs-declare-exports/declaration/dispatch-location")
    }

    "redirect to dispatch location page when 'Standard declaration' is selected" in {

      val correctForm = JsObject(Map("value" -> JsString(AllowedChoiceValues.StandardDec)))
      val Some(result) = route(app, postRequest(choiceUri, correctForm))
      
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/customs-declare-exports/declaration/dispatch-location")
    }

    "redirect to cancel declaration page when 'Cancel declaration' is selected" in {

      val correctForm = JsObject(Map("value" -> JsString(AllowedChoiceValues.CancelDec)))
      val Some(result) = route(app, postRequest(choiceUri, correctForm))

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/customs-declare-exports/cancel-declaration")
    }

    "redirect to submissions page when 'View recent declarations' is selected" in {

      val correctForm = JsObject(Map("value" -> JsString(AllowedChoiceValues.Submissions)))
      val result = route(app, postRequest(choiceUri, correctForm)).get

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/customs-declare-exports/submissions")
    }

    "store a declaration id in the session" in {
      val expectedDeclarationId = "declaration-ABC-123"
      when(mockDeclarationIDGenerator.generateId()).thenReturn(expectedDeclarationId)
      val validChoiceForm = JsObject(Map("value" -> JsString("SMP")))
      val Some(result) = route(app, postRequest(choiceUri, validChoiceForm))
    }
  }
}
