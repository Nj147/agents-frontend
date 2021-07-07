/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.agentsfrontend.controllers.models


import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.agentsfrontend.models.Client


class ClientSpec extends AnyWordSpec with Matchers{

  val clientModel: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = 12,
    postcode = "testPostcode",
    businessType = "testBusinessType",
    arn = "testArn")

  val clientModelJs: JsValue = Json.parse(
    """{
				"crn": "testCrn",
				"name": "testName",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postcode": "testPostcode",
				"businessType": "testBusinessType",
				"arn": "testArn"
			}""".stripMargin)

  "client" can {
    "format to json" should {
      "succeed with ARN" in {
        Json.toJson(clientModel) shouldBe clientModelJs
      }
    }

    "format from json" should {
      "succeed with ARN" in {
        Json.fromJson[Client](clientModelJs) shouldBe JsSuccess(clientModel)
      }
    }
  }

}
