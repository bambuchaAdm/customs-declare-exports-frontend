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
import java.util

import forms.declaration.{Document, PreviousDocumentsData}
import services.cache.ExportsCacheModel
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment
import wco.datamodel.wco.declaration_ds.dms._2.{
  PreviousDocumentCategoryCodeType,
  PreviousDocumentIdentificationIDType,
  PreviousDocumentTypeCodeType
}

import scala.collection.JavaConverters._

object PreviousDocumentsBuilder {

  def buildThenAdd(exportsCacheModel: ExportsCacheModel, goodsShipment: GoodsShipment): Unit =
    exportsCacheModel.previousDocuments.foreach { previousDocumentData =>
      previousDocumentData.documents.foreach { data =>
        goodsShipment.getPreviousDocument.add(createPreviousDocuments(data))
      }
    }

  def build(implicit cacheMap: CacheMap): util.List[GoodsShipment.PreviousDocument] =
    cacheMap
      .getEntry[PreviousDocumentsData](Document.formId)
      .filter(isDefined)
      .map(_.documents.map(createPreviousDocuments))
      .getOrElse(Seq.empty)
      .toList
      .asJava

  private def isDefined(previousDocumentsData: PreviousDocumentsData): Boolean =
    previousDocumentsData.documents.nonEmpty && previousDocumentsData.documents.forall(
      doc =>
        doc.goodsItemIdentifier.getOrElse("").nonEmpty ||
          doc.documentReference.nonEmpty ||
          doc.documentReference.nonEmpty ||
          doc.documentCategory.nonEmpty
    )

  private def createPreviousDocuments(document: Document): GoodsShipment.PreviousDocument = {
    val previousDocument = new GoodsShipment.PreviousDocument()

    if (document.documentCategory.nonEmpty) {
      val categoryCode = new PreviousDocumentCategoryCodeType()
      categoryCode.setValue(document.documentCategory)
      previousDocument.setCategoryCode(categoryCode)
    }

    if (document.documentReference.nonEmpty) {
      val id = new PreviousDocumentIdentificationIDType()
      id.setValue(document.documentReference)
      previousDocument.setID(id)
    }

    if (document.goodsItemIdentifier.getOrElse("").nonEmpty) {
      val lineNumeric = new java.math.BigDecimal(document.goodsItemIdentifier.get)
      previousDocument.setLineNumeric(lineNumeric)
    }

    if (document.documentType.nonEmpty) {
      val typeCode = new PreviousDocumentTypeCodeType()
      typeCode.setValue(document.documentType)
      previousDocument.setTypeCode(typeCode)
    }

    previousDocument
  }
}
