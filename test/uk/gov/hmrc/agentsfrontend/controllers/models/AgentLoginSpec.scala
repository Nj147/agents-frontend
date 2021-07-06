package uk.gov.hmrc.agentsfrontend.controllers.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Format
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.agentsfrontend.models.AgentLogin

class AgentLoginSpec extends AnyWordSpec with Matchers{
  implicit val agentLogin: Format[AgentLogin] = Json.format[AgentLogin]

  val agentLoginModel:AgentLogin = AgentLogin(arn="ABBCDD", password= "ekip")
  val agentLoginModelJs:JsValue = Json.parse("""{"arn": "ABBCDD", "password": "ekip"}""".stripMargin
    )

  "AgentLogin" can {
    "format to json" should{
      "succeed" in{
        Json.toJson(agentLoginModel) shouldBe agentLoginModelJs
      }
    }
    "format from json" should {
      "succeed" in {
        Json.fromJson[AgentLogin](agentLoginModelJs) shouldBe JsSuccess(agentLoginModel)
      }
    }
  }

}
