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

package base

import forms.Choice._
import forms.Choice
import models.{IdentityData, SignedInUser}
import org.joda.time.DateTimeZone.UTC
import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json._
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.ConfidenceLevel.L50
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments, User}

object ExportsTestData {

  val nrsCredentials = Credentials(providerId = "providerId", providerType = "providerType")
  val nrsGroupIdentifierValue = Some("groupIdentifierValue")
  val nrsCredentialRole = Some(User)
  val nrsMdtpInformation = MdtpInformation("deviceId", "sessionId")
  val nrsItmpName = ItmpName(Some("givenName"), Some("middleName"), Some("familyName"))
  val nrsItmpAddress = ItmpAddress(
    Some("line1"),
    Some("line2"),
    Some("line3"),
    Some("line4"),
    Some("line5"),
    Some("postCode"),
    Some("countryName"),
    Some("countryCode")
  )
  val nrsAffinityGroup = Some(Individual)
  val nrsCredentialStrength = Some("STRONG")
  val nrsDateOfBirth = Some(LocalDate.now().minusYears(25))

  val currentLoginTime: DateTime = new DateTime(1530442800000L, UTC)
  val previousLoginTime: DateTime = new DateTime(1530464400000L, UTC)
  val nrsTimeStamp: DateTime = new DateTime(1530475200000L, UTC)

  val nrsLoginTimes = LoginTimes(currentLoginTime, Some(previousLoginTime))

  def newUser(eori: String, externalId: String): SignedInUser =
    SignedInUser(
      eori,
      Enrolments(Set(Enrolment("HMRC-CUS-ORG").withIdentifier("EORINumber", eori))),
      IdentityData(
        Some("Int-ba17b467-90f3-42b6-9570-73be7b78eb2b"),
        Some(externalId),
        None,
        Some(nrsCredentials),
        Some(L50),
        None,
        None,
        Some(Name(Some("Aldo"), Some("Rain"))),
        Some(LocalDate.now().minusYears(25)),
        Some("amina@hmrc.co.uk"),
        Some(AgentInformation(Some("agentId"), Some("agentCode"), Some("agentFriendlyName"))),
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        Some("crdentialStrength 50"),
        Some(LoginTimes(DateTime.now, None))
      )
    )

  val addressJson: JsValue = JsObject(
    Map(
      "fullName" -> JsString("Full name"),
      "building" -> JsString("Building"),
      "street" -> JsString("Street"),
      "townOrCity" -> JsString("Town or City"),
      "postcode" -> JsString("Postcode"),
      "country" -> JsString("Country")
    )
  )

  val goodsPackageJson: JsValue = JsObject(
    Map(
      "commodityCode" -> JsString("Commodity code"),
      "isDescriptionOfYourGoodsCorrect" -> JsBoolean(false),
      "isItemOnUNDGList" -> JsBoolean(false),
      "addLicenceForItem" -> JsBoolean(false),
      "noOfPackages" -> JsString("Number of packages"),
      "packageType" -> JsString("Package type"),
      "goodsInContainer" -> JsBoolean(false),
      "addAnotherPackage" -> JsBoolean(false)
    )
  )

  val jsonBody: JsValue = JsObject(
    Map(
      "ducr" -> JsString("5GB123456789000-123ABC456DEFIIIIIII"),
      "isConsolidateDucrtoWiderShipment" -> JsBoolean(false),
      "mucr" -> JsString(""),
      "isDeclarationForSomeoneElse" -> JsBoolean(false),
      "isAddressAndEORICorrect" -> JsBoolean(false),
      "haveRepresentative" -> JsBoolean(false),
      "isConsignorAddressAndEORICorrect" -> JsBoolean(false),
      "address" -> addressJson,
      "isFinalDestination" -> JsBoolean(false),
      "goodsPackage" -> goodsPackageJson,
      "doYouKnowCustomsProcedureCode" -> JsBoolean(false),
      "customsProcedure" -> JsString("Custom procedure"),
      "wasPreviousCustomsProcedure" -> JsBoolean(false),
      "additionalCustomsProcedure" -> JsString("AdditionalCustomProcedure"),
      "doYouWantAddAdditionalInformation" -> JsBoolean(false),
      "addAnotherItem" -> JsBoolean(false),
      "officeOfExit" -> JsString("Office of exit"),
      "knowConsignmentDispatchCountry" -> JsBoolean(false)
    )
  )

  val cancelJsonBody: JsValue = JsObject(
    Map(
      "wcoDataModelVersionCode" -> JsString("werwr"),
      "wcoTypeName" -> JsString("werewr-"),
      "responsibleAgencyName" -> JsString("werewr"),
      "functionalReferenceID" -> JsString("12"),
      "id" -> JsString("12"),
      "submitter.id" -> JsString("12"),
      "additionalInformation.statementDescription" -> JsString("12"),
      "additionalInformation.statementTypeCode" -> JsString("12"),
      "additionalInformation.pointer.sequenceNumeric" -> JsString("12"),
      "amendment.changeReasonCode" -> JsString("12")
    )
  )

  val wrongJson: JsValue = JsObject(Map("ducr" -> JsString("")))

  val correctDucrJson: JsValue = JsObject(Map("ducr" -> JsString("5GB123456789000-123ABC456DEFIIIII")))

  val wrongMinimumGoodsDate: JsValue = JsObject(
    Map(
      "day" -> JsNumber(0),
      "month" -> JsNumber(0),
      "year" -> JsNumber(LocalDate.now().getYear - 1),
      "hour" -> JsNumber(-1),
      "minute" -> JsNumber(-1)
    )
  )

  val wrongMaximumGoodsDate: JsValue = JsObject(
    Map(
      "day" -> JsNumber(40),
      "month" -> JsNumber(113),
      "year" -> JsNumber(LocalDate.now().getYear),
      "hour" -> JsNumber(25),
      "minute" -> JsNumber(60)
    )
  )

  val goodsDate: JsValue = JsObject(
    Map(
      "day" -> JsNumber(15),
      "month" -> JsNumber(4),
      "year" -> JsNumber(LocalDate.now().getYear),
      "hour" -> JsNumber(16),
      "minute" -> JsNumber(30)
    )
  )

  val emptyLocation: JsValue = JsObject(Map("" -> JsString("")))

  val location: JsValue = JsObject(
    Map(
      "agentLocation" -> JsString("Agent location"),
      "agentRole" -> JsString("Agent role"),
      "goodsLocation" -> JsString("Goods location"),
      "shed" -> JsString("Shed")
    )
  )

  val incorrectTransport: JsValue = JsObject(
    Map(
      "transportId" -> JsString("Transport Id"),
      "transportMode" -> JsString("Transport mode"),
      "transportNationality" -> JsString("Transport nationality")
    )
  )

  val correctTransport: JsValue = JsObject(
    Map(
      "transportId" -> JsString("Transport Id"),
      "transportMode" -> JsString("M"),
      "transportNationality" -> JsString("PL")
    )
  )

  val choiceForm = Json.toJson(Choice("EAL"))
}
