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

package views.declaration

import forms.declaration.CommodityMeasure
import helpers.views.declaration.{CommodityMeasureMessages, CommonMessages}
import play.api.data.Form
import play.twirl.api.Html
import views.declaration.spec.ViewSpec
import views.html.declaration.goods_measure
import views.tags.ViewTest

@ViewTest
class CommodityMeasureViewSpec extends ViewSpec with CommodityMeasureMessages with CommonMessages {

  private val form: Form[CommodityMeasure] = CommodityMeasure.form()
  private val goodsMeasurePage = app.injector.instanceOf[goods_measure]
  private def createView(form: Form[CommodityMeasure] = form): Html =
    goodsMeasurePage(itemId, form)(fakeRequest, messages, appConfig)

  "Commodity Measure View" should {

    "have proper messages for labels" in {

      assertMessage(title, "Commodity measurements")
      assertMessage(netMass, "6/1 Net Weight")
      assertMessage(netMassHint, "The weight excluding container and packaging in kg")
      assertMessage(grossMass, "6/5 Gross Weight")
      assertMessage(grossMassHint, "The weight excluding container and packaging in kg")
      assertMessage(supplementaryUnits, "6/2 Do you need to add supplementary units?")
      assertMessage(
        supplementaryUnitsHint,
        "Enter the quantity in the unit specified by the commodity code, for example 1,000 kWh"
      )
    }

    "have proper messages for error labels" in {

      assertMessage(globalAddOne, "You must add package information to proceed")
      assertMessage(netMassEmpty, "Net weight cannot be empty")
      assertMessage(
        netMassError,
        "Net weight should be a decimal value and should be less than or equal to 99999999999.999 with 3 decimals"
      )
      assertMessage(grossMassEmpty, "Gross weight cannot be empty")
      assertMessage(
        grossMassError,
        "Gross weight should be a decimal value and should be less than or equal to 99999999999999.99 with 2 decimals"
      )
      assertMessage(
        supplementaryUnitsError,
        "Supplementary units should be a decimal value and should be less than or equal to 99999999999999.99 with 2 decimals"
      )
    }
  }

  "Commodity Measure View on empty page" should {

    "display page title" in {

      getElementById(createView(), "title").text() must be(messages(title))
    }

    "display section header" in {

      getElementById(createView(), "section-header").text() must be("Items")
    }

    "display empty input with label for supplementary units" in {

      val view = createView()

      getElementById(view, "supplementaryUnits-label").text() must be(messages(supplementaryUnits))
      getElementById(view, "supplementaryUnits-hint").text() must be(messages(supplementaryUnitsHint))
      getElementById(view, "supplementaryUnits").attr("value") must be("")
    }

    "display empty input with label for net mass" in {

      val view = createView()

      getElementById(view, "netMass-label").text() must be(messages(netMass))
      getElementById(view, "netMass-hint").text() must be(messages(netMassHint))
      getElementById(view, "netMass").attr("value") must be("")
    }

    "display empty input with label for gross mass" in {

      val view = createView()

      getElementById(view, "grossMass-label").text() must be(messages(grossMass))
      getElementById(view, "grossMass-hint").text() must be(messages(grossMassHint))
      getElementById(view, "grossMass").attr("value") must be("")
    }

    "display 'Back' button that links to 'Package Information' page" in {

      val backButton = getElementById(createView(), "link-back")

      backButton.text() must be(messages(backCaption))
      backButton.attr("href") must be(s"/customs-declare-exports/declaration/items/$itemId/package-information")
    }

    "display 'Save and continue' button on page" in {

      val saveButton = getElementByCss(createView(), "#submit")
      saveButton.text() must be(messages(saveAndContinueCaption))
    }
  }

  "Commodity Measure with invalid input" should {

    "display error when nothing is entered" in {

      val view = createView(CommodityMeasure.form().fillAndValidate(CommodityMeasure(Some(""), "", "")))

      checkErrorsSummary(view)
      checkErrorLink(view, 1, netMassEmpty, "#netMass")
      checkErrorLink(view, 2, grossMassEmpty, "#grossMass")

      getElementByCss(view, "#error-message-netMass-input").text() must be(messages(netMassEmpty))
      getElementByCss(view, "#error-message-grossMass-input").text() must be(messages(grossMassEmpty))
    }

    "display error when supplementary units are incorrect" in {

      val view = createView(CommodityMeasure.form().fillAndValidate(CommodityMeasure(Some("99.123"), "", "")))

      checkErrorsSummary(view)
      checkErrorLink(view, 1, supplementaryUnitsError, "#supplementaryUnits")

      getElementByCss(view, "#error-message-supplementaryUnits-input").text() must be(messages(supplementaryUnitsError))
    }

    "display error when net mass is empty" in {

      val view = createView(CommodityMeasure.form().fillAndValidate(CommodityMeasure(Some("99.99"), "", "10.00")))

      checkErrorsSummary(view)
      checkErrorLink(view, 1, netMassEmpty, "#netMass")

      getElementByCss(view, "#error-message-netMass-input").text() must be(messages(netMassEmpty))
    }

    "display error when net mass is incorrect" in {

      val view =
        createView(CommodityMeasure.form().fillAndValidate(CommodityMeasure(Some("99.99"), "20.9999", "10.00")))

      checkErrorsSummary(view)
      checkErrorLink(view, 1, netMassError, "#netMass")

      getElementByCss(view, "#error-message-netMass-input").text() must be(messages(netMassError))
    }

    "display error when gross mass is empty" in {

      val view = createView(CommodityMeasure.form().fillAndValidate(CommodityMeasure(Some("99.99"), "10.00", "")))

      checkErrorsSummary(view)
      checkErrorLink(view, 1, grossMassEmpty, "#grossMass")

      getElementByCss(view, "#error-message-grossMass-input").text() must be(messages(grossMassEmpty))
    }

    "display error when gross mass is incorrect" in {

      val view = createView(CommodityMeasure.form().fillAndValidate(CommodityMeasure(Some("99.99"), "5.00", "100.100")))

      checkErrorsSummary(view)
      checkErrorLink(view, 1, grossMassError, "#grossMass")

      getElementByCss(view, "#error-message-grossMass-input").text() must be(messages(grossMassError))
    }
  }

  "Commodity Measure View when filled" should {

    "display data in supplementary units input" in {

      val form = CommodityMeasure.form().fill(CommodityMeasure(Some("123"), "", ""))
      val view = createView(form)

      getElementById(view, "supplementaryUnits").attr("value") must be("123")
      getElementById(view, "netMass").attr("value") must be("")
      getElementById(view, "grossMass").attr("value") must be("")
    }

    "display data in net mass input" in {

      val form = CommodityMeasure.form().fill(CommodityMeasure(Some(""), "123", ""))
      val view = createView(form)

      getElementById(view, "supplementaryUnits").attr("value") must be("")
      getElementById(view, "netMass").attr("value") must be("123")
      getElementById(view, "grossMass").attr("value") must be("")
    }

    "display data in gross mass input" in {

      val form = CommodityMeasure.form().fill(CommodityMeasure(Some(""), "", "123"))
      val view = createView(form)

      getElementById(view, "supplementaryUnits").attr("value") must be("")
      getElementById(view, "netMass").attr("value") must be("")
      getElementById(view, "grossMass").attr("value") must be("123")
    }

    "display every input filled" in {

      val form = CommodityMeasure.form().fill(CommodityMeasure(Some("123"), "123", "123"))
      val view = createView(form)

      getElementById(view, "supplementaryUnits").attr("value") must be("123")
      getElementById(view, "netMass").attr("value") must be("123")
      getElementById(view, "grossMass").attr("value") must be("123")
    }
  }
}
