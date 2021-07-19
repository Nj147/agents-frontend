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
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.models.Address

class UpdateConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach {

  lazy val connector: UpdateConnector = injector.instanceOf[UpdateConnector]


  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  override val wireMockPort: Int = 9009

  val contactNumber: JsObject = Json.obj("contact-number" -> "0098765345".toLong)
  val correspondence: JsObject = Json.obj("moc" -> List("text"))

  val arn = "ARN00001"
  val address: Address = Address("101a", "AB1 2AB")
  val email: JsObject = Json.obj("email" -> "test@test.com")


  "UpdateContactNumber" should {
    "return true" when {
      "the contact number has been updated" in {
        stubPatch (s"/agents/$arn/contact-number", 200, "")
        val result = connector.updateContactNumber("ARN00001", 98765345678L)
        await(result) shouldBe true
      }
    }
    "return false" when {
      "the contact number has not been updated" in {
        stubPatch(s"/agents/$arn/contact-number", 500, "")
        val result = connector.updateContactNumber("ARN00001", 98765345678L)
        await(result) shouldBe false
      }
    }
  }
  "UpdateCorrespondence" should {
    "return true" when {
      "the correspondence has been updated" in {
        stubPatch (s"/agents/ARN00001/correspondence", 200, Json.toJson(correspondence).toString())
        val result = connector.updateCorrespondence("ARN00001", List("text"))
        await(result) shouldBe true
      }
    }
    "return false" when {
      "the contact number has not been updated" in {
        stubPatch("/agents/ARN00001/correspondence", 500, Json.toJson(correspondence).toString())
        val result = connector.updateCorrespondence("ARN00001", List("text"))
        await(result) shouldBe false
      }
    }
  }

  "updateAddress" should {
    "return a true" when {
      "a valid request is sent and accepted by the backend" in {
        stubPatch(s"/agents/$arn/address", 200, "")
        val result = connector.updateAddress(arn, address)
        await(result) shouldBe true
      }
    }
    "return a false" when {
      "the database responds to the request with a not accepted" in {
        stubPatch(s"/agents/$arn/address", 500, "")
        val result = connector.updateAddress(arn, address)
        await(result) shouldBe false
      }
    }
  }
  "UpdateEmail" should {
    "return true" when {
      "the email has been updated" in {
        stubPatch (s"/agents/ARN00001/update-email", 200, Json.toJson(email).toString())
        val result = connector.updateEmail("ARN00001", "test@test.com")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "the email has not been updated" in {
        stubPatch("/agents/ARN00001/update-email", 500, Json.toJson(email).toString())
        val result = connector.updateEmail("ARN00001", "test@test.com")
        await(result) shouldBe false
      }
    }
  }
}
