package connectors

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.ClientConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentClient

class ClientConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach{
  lazy val connector: ClientConnector = injector.instanceOf[ClientConnector]

  override def beforeEach() = startWireMock()

  override def afterEach() = stopWireMock()

  "POST /create" should {
    "return true when created response returned" in {
      stubPost("/removeClient",202, "")
      val result = connector.removeClient(AgentClient("ARN01234567", "CRN98765432"))
      await(result) shouldBe true
    }
    "return false when created response returned" in {
      stubPost("/removeClient",400, "")
      val result = connector.removeClient(AgentClient("ARN01234567", "CRN98765432"))
      await(result) shouldBe false
    }
  }
}
