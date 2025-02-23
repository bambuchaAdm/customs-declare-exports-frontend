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
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment.GovernmentAgencyGoodsItem.GovernmentProcedure
import wco.datamodel.wco.declaration_ds.dms._2.{GovernmentProcedureCurrentCodeType, GovernmentProcedurePreviousCodeType}

import scala.collection.JavaConverters._

object GovernmentProcedureBuilder {

  def build(
    procedureCodes: Seq[models.declaration.governmentagencygoodsitem.GovernmentProcedure]
  ): java.util.List[GovernmentProcedure] =
    procedureCodes
      .map(procedureCode => createGovernmentProcedure(procedureCode.currentCode, procedureCode.previousCode))
      .toList
      .asJava

  private def createGovernmentProcedure(
    currentCode: Option[String] = None,
    previousCode: Option[String] = None
  ): GovernmentProcedure = {
    val governmentProcedure = new GovernmentProcedure

    currentCode.foreach { value =>
      val currentCodeType = new GovernmentProcedureCurrentCodeType
      currentCodeType.setValue(value)
      governmentProcedure.setCurrentCode(currentCodeType)
    }

    previousCode.foreach { value =>
      val previousCodeType = new GovernmentProcedurePreviousCodeType
      previousCodeType.setValue(value)
      governmentProcedure.setPreviousCode(previousCodeType)
    }

    governmentProcedure
  }
}
