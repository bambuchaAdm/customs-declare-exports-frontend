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

package services.mapping.declaration
import forms.declaration.{TotalNumberOfItems, TotalNumberOfItemsSpec}
import org.scalatest.{Matchers, WordSpec}
import services.cache.ExportsCacheModelBuilder
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration

class InvoiceAmountBuilderSpec extends WordSpec with Matchers with ExportsCacheModelBuilder {

  "InvoiceAmountBuilder" should {
    "correctly map to the WCO-DEC InvoiceAmount instance" in {
      implicit val cacheMap: CacheMap =
        CacheMap(
          "CacheID",
          Map(TotalNumberOfItems.formId -> TotalNumberOfItemsSpec.correctTotalNumberOfItemsDecimalValuesJSON)
        )
      val invoiceAmountType = InvoiceAmountBuilder.build(cacheMap)
      invoiceAmountType.getValue.doubleValue() should be(1212312.12)
      invoiceAmountType.getCurrencyID should be("GBP")
    }

    "build then add" when {
      "no total items" in {
        val model = aCacheModel(withoutTotalNumberOfItems())
        val declaration = new Declaration()

        builder.buildThenAdd(model, declaration)

        declaration.getInvoiceAmount should be(null)
      }

      "empty total amount invoiced" in {
        val model = aCacheModel(withTotalNumberOfItems(totalAmountInvoiced = None))
        val declaration = new Declaration()

        builder.buildThenAdd(model, declaration)

        declaration.getInvoiceAmount should be(null)
      }

      "populated" in {
        val model = aCacheModel(withTotalNumberOfItems(totalAmountInvoiced = Some("123.45")))
        val declaration = new Declaration()

        builder.buildThenAdd(model, declaration)

        declaration.getInvoiceAmount.getValue.doubleValue should be(123.45)
        declaration.getInvoiceAmount.getCurrencyID should be("GBP")
      }
    }
  }

  private def builder = new InvoiceAmountBuilder()
}
