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
import forms.declaration.destinationCountries.DestinationCountries
import services.Countries.allCountries
import services.cache.ExportsCacheModel
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment.Destination
import wco.datamodel.wco.declaration_ds.dms._2.DestinationCountryCodeType

object DestinationBuilder {

  def build(implicit cacheMap: CacheMap, choice: Choice): GoodsShipment.Destination =
    cacheMap
      .getEntry[DestinationCountries](DestinationCountries.formId)
      .filter(data => isDefined(data.countryOfDestination))
      .map(data => createExportCountry(data.countryOfDestination))
      .orNull

  def buildThenAdd(exportsCacheModel: ExportsCacheModel, goodsShipment: GoodsShipment) =
    exportsCacheModel.locations.destinationCountries.foreach { destinationCountries =>
      goodsShipment.setDestination(createExportCountry(destinationCountries.countryOfDestination))
    }

  private def isDefined(countryOfDestination: String): Boolean = countryOfDestination.nonEmpty

  private def createExportCountry(countryOfDestination: String): GoodsShipment.Destination = {

    val countryCode = new DestinationCountryCodeType()
    countryCode.setValue(
      allCountries
        .find(country => countryOfDestination.contains(country.countryCode))
        .map(_.countryCode)
        .getOrElse("")
    )

    val destination = new Destination()
    destination.setCountryCode(countryCode)
    destination
  }
}
