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

package services.mapping.goodsshipment.consignment
import forms.declaration.WarehouseIdentification
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment.Consignment.ArrivalTransportMeans
import wco.datamodel.wco.declaration_ds.dms._2.ArrivalTransportMeansModeCodeType

object ArrivalTransportMeansBuilder {

  def build(implicit cacheMap: CacheMap): GoodsShipment.Consignment.ArrivalTransportMeans =
    cacheMap
      .getEntry[WarehouseIdentification](WarehouseIdentification.formId)
      .filter(_.inlandModeOfTransportCode.isDefined)
      .map(createArrivalTrasportMeans)
      .orNull

  private def createArrivalTrasportMeans(transportInformation: WarehouseIdentification) = {
    val arrivalTransportMeans = new ArrivalTransportMeans()

    transportInformation.inlandModeOfTransportCode.foreach { value =>
      val modeCodeType = new ArrivalTransportMeansModeCodeType()
      modeCodeType.setValue(value)
      arrivalTransportMeans.setModeCode(modeCodeType)
    }

    arrivalTransportMeans
  }
}
