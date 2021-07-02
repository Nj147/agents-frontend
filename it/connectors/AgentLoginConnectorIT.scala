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
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.AgentConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentLogin

class AgentLoginConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach {
  lazy val connector: AgentConnector = injector.instanceOf[AgentConnector]

  override def beforeEach(): Unit = startWireMock()
  override def afterEach(): Unit = stopWireMock()

  private val testAgentLogin = AgentLogin("FJ3J343J", "pa55w0rd")

  override val wireMockPort: Int = 9009

  "checkLogin" should {
    "return true" when {
      "agent login details exist" in {
        stubPost("/check-agent-login", 200, "")
        val result = connector.checkLogin(testAgentLogin)
        await(result) shouldBe true
      }
    }
  }

  "checkLogin" should {
    "return false" when {
      "agent login details do not exist" in {
        stubPost("/check-agent-login", 500, "")
        val result = connector.checkLogin(testAgentLogin)
        await(result) shouldBe false
      }
    }
  }

}