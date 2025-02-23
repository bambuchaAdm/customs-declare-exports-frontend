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
import org.scalatest.{Matchers, WordSpec}
import services.cache.ExportsCacheModelBuilder
import wco.datamodel.wco.dec_dms._2.Declaration

class FunctionCodeBuilderSpec extends WordSpec with Matchers with ExportsCacheModelBuilder {

  "FunctionCodeBuilder" should {
    "correctly map to the WCO-DEC Declaration Function Code instance" in {
      val model = aCacheModel()
      val declaration = new Declaration()

      new FunctionCodeBuilder().buildThenAdd(model, declaration)

      declaration.getFunctionCode.getValue should be("9")
    }

    "build" in {
      FunctionCodeBuilder.build("1").getValue should be("1")
    }
  }
}
