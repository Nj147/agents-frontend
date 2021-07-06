package uk.gov.hmrc.agentsfrontend.controllers.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.agentsfrontend.models.AgentClient


class AgentSpec extends AnyWordSpec with Matchers{

  val agentClientModel:AgentClient = AgentClient(arn="ABBCD", crn= "ABBDCD")
  val agentClientModelJs:JsValue = Json.parse("""{"arn": "ABBCD", "crn": "ABBDCD"}""".stripMargin
  )

  "AgentClient" can {
    "format to json" should{
      "succeed" in{
        Json.toJson(agentClientModel) shouldBe agentClientModelJs
      }
    }
    "format from json" should {
      "succeed" in {
        Json.fromJson[AgentClient](agentClientModelJs) shouldBe JsSuccess(agentClientModel)
      }
    }
  }

}
