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

import forms.declaration.AdditionalFiscalReference
import org.scalatest.{Matchers, WordSpec}
import wco.datamodel.wco.dec_dms._2.Declaration.GoodsShipment.GovernmentAgencyGoodsItem.DomesticDutyTaxParty

class DomesticDutyTaxPartyBuilderSpec extends WordSpec with Matchers with GovernmentAgencyGoodsItemData {

  "DomesticDutyTaxPartyBuilder" should {
    "map correctly if cache contains Additional Fiscal References" in {

      val domesticDutyTaxParties: java.util.List[DomesticDutyTaxParty] =
        DomesticDutyTaxPartyBuilder.build(Seq(AdditionalFiscalReference("PL", "12345")))
      domesticDutyTaxParties.size() should be(1)
      domesticDutyTaxParties.get(0).getID.getValue should be("PL12345")
      domesticDutyTaxParties.get(0).getRoleCode.getValue should be("FR1")
    }

    "map correctly if cache contains more than one Additional Fiscal References" in {

      val domesticDutyTaxParties: java.util.List[DomesticDutyTaxParty] = DomesticDutyTaxPartyBuilder.build(
        Seq(AdditionalFiscalReference("PL", "12345"), AdditionalFiscalReference("FR", "54321"))
      )

      domesticDutyTaxParties.size() should be(2)
      domesticDutyTaxParties.get(0).getID.getValue should be("PL12345")
      domesticDutyTaxParties.get(0).getRoleCode.getValue should be("FR1")
      domesticDutyTaxParties.get(1).getID.getValue should be("FR54321")
      domesticDutyTaxParties.get(1).getRoleCode.getValue should be("FR1")
    }

    "should not create list of DomesticDutyTaxParty if user doesn't add any Additional Fiscal References" in {

      val domesticDutyTaxParties: java.util.List[DomesticDutyTaxParty] = DomesticDutyTaxPartyBuilder.build(Seq.empty)

      domesticDutyTaxParties.isEmpty shouldBe true
    }
  }
}
