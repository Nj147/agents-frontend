import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.DashBoardConnector
import uk.gov.hmrc.agentsfrontend.models.Client

class DashBoardConnectorSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach{
 lazy val connector:DashBoardConnector = injector.instanceOf[DashBoardConnector]

  override def beforeEach(): Unit = startWireMock()
  override def afterEach(): Unit = stopWireMock()

  val clientList = Json.toJson(List( Client("AABCCD", "Elon Musk", "SpaceX", "08977643456", 8, "BS166FGJ", "Space Exploration", "ABBCVDDE"), Client("AADSCCD", "Elon Musk", "SpaceX", "08977643456", 7, "BS166FGJ","Space Exploration", "AVVCVDDE"))).toString()
  "getsAllClientsData" should {
    "return 200" when {
      "Clients data successfully fetched" in {
        stubPost("/readAllAgent", 200, clientList)
        val result = connector.getAllClientsData
        await(result) shouldBe (List( Client("AABCCD", "Elon Musk", "SpaceX", "08977643456", 8, "BS166FGJ", "Space Exploration", "ABBCVDDE"), Client("AADSCCD", "Elon Musk", "SpaceX", "08977643456", 7, "BS166FGJ","Space Exploration", "AVVCVDDE")))
      }
    }

    "return 404" when {
      "Clients data not fetched" in {
        stubPost("/readAllAgent", 404, "")
        val result = connector.getAllClientsData
        await(result) shouldBe List()
      }
    }
  }
}


