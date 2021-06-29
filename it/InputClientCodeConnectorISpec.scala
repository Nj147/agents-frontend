import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.{GuiceOneAppPerSuite, GuiceOneServerPerSuite}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.InputClientCodeConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentClient

class InputClientCodeConnectorISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach{
  lazy val connector: InputClientCodeConnector = injector.instanceOf[InputClientCodeConnector]

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  "postClientCode" should {
    "return 204" when {
      "Agent code is successfully added" in {
        stubPost("/addAgent", 204, "")
        val result = connector.postClientCode(agentClientCode = AgentClient("Agent", "Client"))
        await(result) shouldBe(204)
      }
    }
    "return 404" when {
      "Client code provided does not exist in Client database" in {
        stubPost("/addAgent", 404, "")
        val result = connector.postClientCode(agentClientCode = AgentClient("Agent", "Error in Client Code"))
        await(result) shouldBe(404)
      }
    }
    "return 409" when {
      "Client already has an associated Agent" in {
        stubPost("/addAgent", 409, "")
        val result = connector.postClientCode(agentClientCode = AgentClient("Agent", "Client code already in use"))
        await(result) shouldBe(409)
      }
    }
  }
}
