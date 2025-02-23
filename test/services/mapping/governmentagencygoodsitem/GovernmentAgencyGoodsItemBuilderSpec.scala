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

package services.mapping.governmentagencygoodsitem

import forms.declaration.{AdditionalFiscalReference, AdditionalFiscalReferencesData}
import models.declaration.governmentagencygoodsitem.{Commodity => _, GovernmentProcedure => _, Packaging => _}
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json._
import services.ExportsItemsCacheIds
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment.{
  GovernmentAgencyGoodsItem => WCOGovernmentAgencyGoodsItem
}

class GovernmentAgencyGoodsItemBuilderSpec extends WordSpec with Matchers with GovernmentAgencyGoodsItemData {

  "GovernmentAgencyGoodsItemBuilder" should {
    "map to WCO model correctly " in {
      implicit val cacheMap: CacheMap =
        CacheMap("CacheID", Map(ExportsItemsCacheIds.itemsId -> GovernmentAgencyGoodsItemBuilderSpec.itemsJsonList))

      val agencyGoodsItems: java.util.List[WCOGovernmentAgencyGoodsItem] = GovernmentAgencyGoodsItemBuilder.build
      agencyGoodsItems.isEmpty shouldBe false

      validateAdditionalDocuments(agencyGoodsItems.get(0).getAdditionalDocument.get(0))
      validateAdditionalInformation(agencyGoodsItems.get(0).getAdditionalInformation.get(0))
      validateCommodity(agencyGoodsItems.get(0).getCommodity)
      validatePackaging(agencyGoodsItems.get(0).getPackaging.get(0))
      validateGovernmentProcedure(agencyGoodsItems.get(0).getGovernmentProcedure.get(0))
    }

    "map correctly if ItemType is None " in {
      implicit val cacheMap: CacheMap =
        CacheMap(
          "CacheID",
          Map(ExportsItemsCacheIds.itemsId -> GovernmentAgencyGoodsItemBuilderSpec.emptyItemsJsonList)
        )

      val mappedGoodsItemList: java.util.List[WCOGovernmentAgencyGoodsItem] = GovernmentAgencyGoodsItemBuilder.build
      mappedGoodsItemList.isEmpty shouldBe true
    }
  }

  private def validateGovernmentProcedure(mappedProcedure: WCOGovernmentAgencyGoodsItem.GovernmentProcedure) = {
    mappedProcedure.getCurrentCode.getValue shouldBe cachedCode.substring(0, 2)
    mappedProcedure.getPreviousCode.getValue shouldBe cachedCode.substring(2, 4)
  }

  private def validatePackaging(packaging: WCOGovernmentAgencyGoodsItem.Packaging) = {
    packaging.getQuantityQuantity.getValue shouldBe BigDecimal(2).bigDecimal
    packaging.getMarksNumbersID.getValue shouldBe "mark1"
    packaging.getTypeCode.getValue shouldBe "AA"
  }

  private def validateCommodity(mappedCommodity: WCOGovernmentAgencyGoodsItem.Commodity) = {
    mappedCommodity.getDescription.getValue shouldBe "commodityDescription"
    mappedCommodity.getDangerousGoods.get(0).getUNDGID.getValue shouldBe "999"

    val goodsMeasure = mappedCommodity.getGoodsMeasure

    goodsMeasure.getNetNetWeightMeasure.getValue shouldBe BigDecimal(90).bigDecimal
    goodsMeasure.getNetNetWeightMeasure.getUnitCode shouldBe ExportsItemsCacheIds.defaultMeasureCode

    goodsMeasure.getGrossMassMeasure.getValue shouldBe BigDecimal(100).bigDecimal
    goodsMeasure.getGrossMassMeasure.getUnitCode shouldBe ExportsItemsCacheIds.defaultMeasureCode

    goodsMeasure.getTariffQuantity.getValue shouldBe BigDecimal(2).bigDecimal
    goodsMeasure.getTariffQuantity.getUnitCode shouldBe ExportsItemsCacheIds.defaultMeasureCode
  }
  private def validateAdditionalInformation(
    additionalInfomation: WCOGovernmentAgencyGoodsItem.AdditionalInformation
  ) = {
    additionalInfomation.getStatementCode.getValue shouldBe statementCode
    additionalInfomation.getStatementDescription.getValue shouldBe descriptionValue
  }

  private def validateAdditionalDocuments(firstMappedDocument: WCOGovernmentAgencyGoodsItem.AdditionalDocument) = {
    firstMappedDocument.getCategoryCode.getValue shouldBe documentAndAdditionalDocumentTypeCode.substring(0, 1)
    firstMappedDocument.getTypeCode.getValue shouldBe documentAndAdditionalDocumentTypeCode.substring(1)
    firstMappedDocument.getID.getValue shouldBe documentIdentifier + documentPart
    firstMappedDocument.getLPCOExemptionCode.getValue shouldBe "PND"
    firstMappedDocument.getName.getValue shouldBe documentStatus + documentStatusReason
    firstMappedDocument.getSubmitter.getName.getValue shouldBe issusingAuthorityName

    val writeoff = firstMappedDocument.getWriteOff
    writeoff.getAmountAmount shouldBe null
    val writeOffQuantity = writeoff.getQuantityQuantity
    writeOffQuantity.getUnitCode shouldBe "KGM"
    writeOffQuantity.getValue shouldBe documentQuantity.bigDecimal
  }
}

object GovernmentAgencyGoodsItemBuilderSpec {
  val firstPackagingJson: JsValue = JsObject(
    Map(
      "sequenceNumeric" -> JsNumber(0),
      "marksNumbersId" -> JsString("mark1"),
      "quantity" -> JsNumber(2),
      "typeCode" -> JsString("AA")
    )
  )
  val secondPackagingJson: JsValue = JsObject(
    Map(
      "sequenceNumeric" -> JsNumber(1),
      "marksNumbersId" -> JsString("mark2"),
      "quantity" -> JsNumber(4),
      "typeCode" -> JsString("AB")
    )
  )
  val writeOffQuantityMeasure: JsValue = JsObject(Map("unitCode" -> JsString("KGM"), "value" -> JsString("10")))
  val writeOffAmount: JsValue = JsObject(Map("currencyId" -> JsString("GBP"), "value" -> JsString("100")))
  val writeOff: JsValue = JsObject(Map("quantity" -> writeOffQuantityMeasure, "amount" -> writeOffAmount))
  val dateTimeString: JsValue = JsObject(Map("formatCode" -> JsString("102"), "value" -> JsString("20170101")))
  val dateTimeElement: JsValue = JsObject(Map("dateTimeString" -> dateTimeString))
  val documentSubmitter: JsValue = JsObject(
    Map("name" -> JsString("issuingAuthorityName"), "roleCode" -> JsString("SubmitterRoleCode"))
  )
  val amount: JsValue = JsObject(Map("currencyId" -> JsString("GBP"), "value" -> JsString("100")))
  val additionalInformations: JsValue = JsObject(
    Map("code" -> JsString("code"), "description" -> JsString("description"))
  )
  val firstGovernmentProcedure: JsValue = JsObject(
    Map("currentCode" -> JsString("CU"), "previousCode" -> JsString("PR"))
  )
  val secondGovernmentProcedure: JsValue = JsObject(Map("currentCode" -> JsString("CC"), "previousCode" -> JsNull))
  val thirdGovernmentProcedure: JsValue = JsObject(Map("currentCode" -> JsString("PR"), "previousCode" -> JsNull))

  val grossMassMeasureJson: JsValue = JsObject(Map("unitCode" -> JsString("kg"), "value" -> JsString("100")))
  val netWeightMeasureJson: JsValue = JsObject(Map("unitCode" -> JsString("kg"), "value" -> JsString("90")))
  val tariffQuantityJson: JsValue = JsObject(Map("unitCode" -> JsString("kg"), "value" -> JsString("2")))
  val goodsMeasureJson: JsValue = JsObject(
    Map(
      "grossMassMeasure" -> grossMassMeasureJson,
      "netWeightMeasure" -> netWeightMeasureJson,
      "tariffQuantity" -> tariffQuantityJson
    )
  )
  val dangerousGoodsJson: JsValue = JsObject(Map("undgid" -> JsString("999")))
  val firstClassificationsJson: JsValue = JsObject(
    Map(
      "id" -> JsString("classificationsId"),
      "nameCode" -> JsString("nameCodeId"),
      "identificationTypeCode" -> JsString("123"),
      "bindingTariffReferenceId" -> JsString("bindingTariffReferenceId")
    )
  )
  val secondClassificationsJson: JsValue = JsObject(
    Map(
      "id" -> JsString("classificationsId2"),
      "nameCode" -> JsString("nameCodeId2"),
      "identificationTypeCode" -> JsString("321"),
      "bindingTariffReferenceId" -> JsString("bindingTariffReferenceId2")
    )
  )
  val commodityJson: JsValue = JsObject(
    Map(
      "description" -> JsString("commodityDescription"),
      "classifications" -> JsArray(Seq(firstClassificationsJson, secondClassificationsJson)),
      "dangerousGoods" -> JsArray(Seq(dangerousGoodsJson)),
      "goodsMeasure" -> goodsMeasureJson
    )
  )

  val additionalDocuments: JsValue = JsObject(
    Map(
      "categoryCode" -> JsString("C"),
      "effectiveDateTime" -> dateTimeElement,
      "id" -> JsString("SYSUYSU12324554"),
      "name" -> JsString("PENDINGReason"),
      "typeCode" -> JsString("501"),
      "lpcoExemptionCode" -> JsString("PND"),
      "submitter" -> documentSubmitter,
      "writeOff" -> writeOff
    )
  )

  val governmentAgencyGoodsItemJson: JsValue = JsObject(
    Map(
      "sequenceNumeric" -> JsNumber(1),
      "statisticalValueAmount" -> amount,
      "commodity" -> commodityJson,
      "additionalInformations" -> JsArray(Seq(additionalInformations)),
      "additionalDocuments" -> JsArray(Seq(additionalDocuments)),
      "governmentProcedures" -> JsArray(
        Seq(firstGovernmentProcedure, secondGovernmentProcedure, thirdGovernmentProcedure)
      ),
      "packagings" -> JsArray(Seq(firstPackagingJson, secondPackagingJson)),
      "fiscalReferences" -> JsArray(
        Seq(
          Json.toJson(AdditionalFiscalReference("PL", "12345")),
          Json.toJson(AdditionalFiscalReference("FR", "54321"))
        )
      )
    )
  )

  val itemsJsonList = JsArray(Seq(governmentAgencyGoodsItemJson))
  val emptyItemsJsonList = JsArray(Seq())
}
