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
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.AgentDetailsConnector
import uk.gov.hmrc.agentsfrontend.models.{Address, AgentDetails}

class AgentDetailsConnectorIt extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach {
  lazy val connector: AgentDetailsConnector = injector.instanceOf[AgentDetailsConnector]

  override def beforeEach(): Unit = startWireMock()
  override def afterEach(): Unit = stopWireMock()

  private val agentToUpdate = AgentDetails("ARN3432", "Business Ltd", "john@gmail.com", "0732838243".toLong, List("Phone call", "Text message"), "12", "SW23232")

  override val wireMockPort: Int = 9009

  "updateAgent" should {
    "return true" when {
      "update is successful" in {
        stubPut("/update-agent", 202, "")
        val result = connector.updateAgent(agentToUpdate)
        await(result) shouldBe true
      }
    }
  }

  "updateAgent" should {
    "return false" when {
      "update is unsuccessful" in {
        stubPut("/update-agent", 500, "")
        val result = connector.updateAgent(agentToUpdate)
        await(result) shouldBe false
      }
    }
  }


}
