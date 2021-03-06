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

package uk.gov.hmrc.agentsfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Format
import play.api.libs.json.{JsSuccess, JsValue, Json}

class AgentLoginSpec extends AnyWordSpec with Matchers {
  implicit val agentLogin: Format[AgentLogin] = Json.format[AgentLogin]

  val agentLoginModel: AgentLogin = AgentLogin(arn = "ABBCDD", password = "ekip")
  val agentLoginModelJs: JsValue = Json.parse("""{"arn": "ABBCDD", "password": "ekip"}""".stripMargin
  )

  "AgentLogin" can {
    "format to json" should {
      "succeed" in {
        Json.toJson(agentLoginModel) shouldBe agentLoginModelJs
      }
    }
    "format from json" should {
      "succeed" in {
        Json.fromJson[AgentLogin](agentLoginModelJs) shouldBe JsSuccess(agentLoginModel)
      }
    }
  }

}
