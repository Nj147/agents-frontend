package connectors

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

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.DashBoardConnector
import uk.gov.hmrc.agentsfrontend.models.Client

class DashBoardConnectorSpec extends AnyWordSpec
  with Matchers
  with GuiceOneServerPerSuite
  with WireMockHelper
  with BeforeAndAfterEach {

  lazy val connector: DashBoardConnector = injector.instanceOf[DashBoardConnector]

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  val clientList: String = Json.toJson(Some(List(Client("AABCCD", "Elon Musk", "SpaceX", "08977643456", 8, "BS166FGJ", "Space Exploration", "ABBCVDDE"),
    Client("AADSCCD", "Elon Musk", "SpaceX", "08977643456", 7, "BS166FGJ", "Space Exploration", "AVVCVDDE")))).toString()

  "getsAllClientsData" should {
    "return 200" when {
      "Clients data successfully fetched" in {
        stubPost("/read-all-agent", 200, clientList)
        val result = connector.getAllClientsData(arn = "AADSCCD")
        await(result) shouldBe Some(List(Client("AABCCD", "Elon Musk", "SpaceX", "08977643456", 8, "BS166FGJ", "Space Exploration", "ABBCVDDE"),
          Client("AADSCCD", "Elon Musk", "SpaceX", "08977643456", 7, "BS166FGJ", "Space Exploration", "AVVCVDDE")))
      }
    }

    "return 404" when {
      "Clients data not fetched" in {
        stubPost("/read-all-agent", 404, "")
        val result = connector.getAllClientsData(arn = "")
        await(result) shouldBe Some(List())
      }
    }
  }
}


