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
import forms.declaration.DeclarationAdditionalActors
import models.declaration.DeclarationAdditionalActorsDataSpec
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment

class AEOMutualRecognitionPartiesBuilderSpec extends WordSpec with Matchers with MockitoSugar {

  "AEOMutualRecognitionPartiesBuilder " should {

    "correctly map new model to a WCO-DEC GoodsShipment.AEOMutualRecognitionParties instance" when {
      "all data has been supplied" in {
        val builder = new AEOMutualRecognitionPartiesBuilder
        val goodsShipment = new GoodsShipment
        builder.buildThenAdd(DeclarationAdditionalActorsDataSpec.correctAdditionalActorsData.actors.head, goodsShipment)

        val actors = goodsShipment.getAEOMutualRecognitionParty
        actors.size should be(1)
        actors.get(0).getID.getValue should be("eori1")
        actors.get(0).getRoleCode.getValue should be("CS")
      }

      "'eori' has not been supplied" in {
        val builder = new AEOMutualRecognitionPartiesBuilder
        val goodsShipment = new GoodsShipment
        builder.buildThenAdd(DeclarationAdditionalActors(None, Some("CS")), goodsShipment)

        val actors = goodsShipment.getAEOMutualRecognitionParty
        actors.size should be(1)
        actors.get(0).getID should be(null)
        actors.get(0).getRoleCode.getValue should be("CS")
      }

      "'partyType' has not been supplied" in {
        val builder = new AEOMutualRecognitionPartiesBuilder
        val goodsShipment = new GoodsShipment
        builder.buildThenAdd(DeclarationAdditionalActors(Some("eori1"), None), goodsShipment)

        val actors = goodsShipment.getAEOMutualRecognitionParty
        actors.size should be(1)
        actors.get(0).getID.getValue should be("eori1")
        actors.get(0).getRoleCode should be(null)
      }
    }
  }

}
