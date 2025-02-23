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
import forms.declaration.{RepresentativeDetails, RepresentativeDetailsSpec}
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap

class AgentBuilderSpec extends WordSpec with Matchers {

  "AgentBuilder" should {
    "correctly map to the WCO-DEC Agent instance" when {
      "only EORI is supplied" in {
        implicit val cacheMap: CacheMap =
          CacheMap(
            "CacheID",
            Map(RepresentativeDetails.formId -> RepresentativeDetailsSpec.correctRepresentativeDetailsEORIOnlyJSON)
          )
        val agent = AgentBuilder.build(cacheMap)
        agent.getID.getValue should be("9GB1234567ABCDEF")
        agent.getName should be(null)
        agent.getAddress should be(null)
        agent.getFunctionCode.getValue should be("2")
      }

      "only Address is supplied" in {
        implicit val cacheMap: CacheMap =
          CacheMap(
            "CacheID",
            Map(
              RepresentativeDetails.formId -> Json
                .toJson(RepresentativeDetailsSpec.correctRepresentativeDetailsAddressOnly)
            )
          )
        val agent = AgentBuilder.build(cacheMap)
        agent.getID should be(null)
        agent.getName.getValue should be("Full Name")
        agent.getAddress.getLine.getValue should be("Address Line")
        agent.getAddress.getCityName.getValue should be("Town or City")
        agent.getAddress.getCountryCode.getValue should be("PL")
        agent.getAddress.getPostcodeID.getValue should be("AB12 34CD")
        agent.getFunctionCode.getValue should be("2")
      }
    }
  }
}
