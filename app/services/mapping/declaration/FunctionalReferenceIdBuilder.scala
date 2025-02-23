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
import forms.declaration.ConsignmentReferences
import javax.inject.Inject
import services.cache.ExportsCacheModel
import services.mapping.ModifyingBuilder
import uk.gov.hmrc.http.cache.client.CacheMap
import wco.datamodel.wco.dec_dms._2.Declaration
import wco.datamodel.wco.declaration_ds.dms._2.DeclarationFunctionalReferenceIDType

class FunctionalReferenceIdBuilder @Inject()() extends ModifyingBuilder[Declaration] {

  override def buildThenAdd(exportsCacheModel: ExportsCacheModel, declaration: Declaration) {
    exportsCacheModel.consignmentReferences.foreach(references => {
      if (references.lrn.nonEmpty) {
        val declarationFunctionalReferenceIdType = new DeclarationFunctionalReferenceIDType()
        declarationFunctionalReferenceIdType.setValue(references.lrn)
        declaration.setFunctionalReferenceID(declarationFunctionalReferenceIdType)
      }
    })
  }

}

object FunctionalReferenceIdBuilder {
  def build(implicit cacheMap: CacheMap): DeclarationFunctionalReferenceIDType =
    cacheMap
      .getEntry[ConsignmentReferences](ConsignmentReferences.id)
      .filter(data => data.lrn.nonEmpty)
      .map(createFunctionalReferenceId)
      .orNull

  def build(functionalReferenceId: String): DeclarationFunctionalReferenceIDType = {
    val referenceId = new DeclarationFunctionalReferenceIDType()
    referenceId.setValue(functionalReferenceId)
    referenceId
  }
  private def createFunctionalReferenceId(data: ConsignmentReferences): DeclarationFunctionalReferenceIDType = {
    val referenceId = new DeclarationFunctionalReferenceIDType()
    referenceId.setValue(data.lrn)
    referenceId
  }

}
