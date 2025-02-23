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

package services.mapping.goodsshipment
import forms.Choice
import services.cache.ExportsCacheModel
import services.mapping.goodsshipment.consignment.ConsignmentBuilder
import services.mapping.governmentagencygoodsitem.GovernmentAgencyGoodsItemBuilder
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment

object GoodsShipmentBuilder {

  def build(implicit cacheMap: CacheMap, choice: Choice): GoodsShipment = {
    val goodsShipment = new GoodsShipment()

    goodsShipment.setTransactionNatureCode(GoodsShipmentNatureOfTransactionBuilder.build)
    goodsShipment.setConsignee(ConsigneeBuilder.build)
    goodsShipment.setConsignment(ConsignmentBuilder.build)
    goodsShipment.setDestination(DestinationBuilder.build)
    goodsShipment.setExportCountry(ExportCountryBuilder.build)
    goodsShipment.setUCR(UCRBuilder.build)
    goodsShipment.setWarehouse(WarehouseBuilder.build)
    goodsShipment.getPreviousDocument
      .addAll(PreviousDocumentsBuilder.build)
    goodsShipment.getGovernmentAgencyGoodsItem.addAll(GovernmentAgencyGoodsItemBuilder.build)
    goodsShipment.getAEOMutualRecognitionParty.addAll(AEOMutualRecognitionPartiesBuilder.build)

    goodsShipment
  }

  def build(exportsCacheModel: ExportsCacheModel): GoodsShipment = {
    val goodsShipment = new GoodsShipment()

    GoodsShipmentNatureOfTransactionBuilder.buildThenAdd(exportsCacheModel.natureOfTransaction, goodsShipment)
    ConsigneeBuilder.buildThenAdd(exportsCacheModel.parties.consigneeDetails, goodsShipment)
    ConsignmentBuilder.buildThenAdd(exportsCacheModel, goodsShipment)
    DestinationBuilder.buildThenAdd(exportsCacheModel, goodsShipment)
    ExportCountryBuilder.buildThenAdd(exportsCacheModel, goodsShipment)
    UCRBuilder.buildThenAdd(exportsCacheModel, goodsShipment)
    WarehouseBuilder.buildThenAdd(exportsCacheModel, goodsShipment)
    PreviousDocumentsBuilder.buildThenAdd(exportsCacheModel, goodsShipment)
    //    goodsShipment.getGovernmentAgencyGoodsItem.addAll(GovernmentAgencyGoodsItemBuilder.build)
    AEOMutualRecognitionPartiesBuilder.buildThenAdd(exportsCacheModel, goodsShipment)

    goodsShipment
  }
}
