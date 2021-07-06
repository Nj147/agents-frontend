package uk.gov.hmrc.agentsfrontend.controllers.models


import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.agentsfrontend.models.Client


class ClientSpec extends AnyWordSpec with Matchers{

  val testClient: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = 12,
    postcode = "testPostcode",
    businessType = "testBusinessType",
    arn = "testArn")

  val testClientJs: JsValue = Json.parse(
    """{
				"crn": "testCrn",
				"name": "testName",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postcode": "testPostcode",
				"businessType": "testBusinessType",
				"arn": "testArn"
			}""".stripMargin)

  val testClientJsNone: JsValue = Json.parse(
    """{
				"crn": "testCrn",
				"name": "testName",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postcode": "testPostcode",
				"businessType": "testBusinessType"
			}""".stripMargin)

  "client" can {
    "format to json" should {
      "succeed with ARN" in {
        Json.toJson(testClient) shouldBe testClientJs
      }

    }

    "format from json" should {
      "succeed with ARN" in {
        Json.fromJson[Client](testClientJs) shouldBe JsSuccess(testClient)
      }

    }
  }


}
