package uk.gov.hmrc.agentsfrontend.controllers


import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.DashBoardConnector

class DashBoardConnectorSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach{
 lazy val connector:DashBoardConnector = injector.instanceOf[DashBoardConnector]

  override def beforeEach(): Unit = startWireMock()
  override def afterEach(): Unit = stopWireMock()

  "getsAllClientsData" should {
    "return 200" when {
      "Clients data successfully fetched" in {
        stubPost("/readAllAgent", 200, "")
        val result = connector.getAllClientsData()
        await(result) shouldBe (200)
      }
    }

    "return 404" when {
      "Clients data not fetched" in {
        stubPost("/readAllAgent", 404, "")
        val result = connector.getAllClientsData()
        await(result) shouldBe (404)
      }
    }
  }
}

