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