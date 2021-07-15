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

package connectors

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.AgentDetailsConnector
import uk.gov.hmrc.agentsfrontend.models.AgentDetails

class AgentDetailsConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach {

  lazy val connector: AgentDetailsConnector = injector.instanceOf[AgentDetailsConnector]

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  override val wireMockPort: Int = 9009

  def agent: AgentDetails = AgentDetails("ARN00000", "testBusinessName", "testEmail", 0x8, List("test"), "testAddressLine1", "testPostcode")

  "getDetails" should {
    "return list of details on the agent" in {
      stubPost(s"/agents/${agent.arn}/details", 200, Json.toJson(agent).toString())
      val result = connector.getAgentDetails(arn = "ARN00000")
      await(result) shouldBe Some(agent)
    }
  }


}
