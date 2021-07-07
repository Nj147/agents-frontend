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
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.agentsfrontend.models.AgentClient


class AgentSpec extends AnyWordSpec with Matchers{

  val agentClientModel:AgentClient = AgentClient(arn="ABBCD", crn= "ABBDCD")
  val agentClientModelJs:JsValue = Json.parse("""{"arn": "ABBCD", "crn": "ABBDCD"}""".stripMargin
  )

  "AgentClient" can {
    "format to json" should{
      "succeed" in{
        Json.toJson(agentClientModel) shouldBe agentClientModelJs
      }
    }
    "format from json" should {
      "succeed" in {
        Json.fromJson[AgentClient](agentClientModelJs) shouldBe JsSuccess(agentClientModel)
      }
    }
  }

}
