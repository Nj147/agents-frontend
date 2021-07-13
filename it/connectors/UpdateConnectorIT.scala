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
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.models.UpdateContactNumber

class UpdateConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach {

  lazy val connector: UpdateConnector = injector.instanceOf[UpdateConnector]

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  override val wireMockPort: Int = 9009

  val contactNumber: UpdateContactNumber = UpdateContactNumber("ARN00001", "0098765345".toLong)
  "UpdateContactNumber" should {
    "return true" when {
      "the contact number has been updated" in {
        stubPatch("/update-contact-number", 204, Json.toJson(contactNumber).toString())
        val result = connector.updateContactNumber(contactNumber)
        await(result) shouldBe true
      }
    }
    "return false" when {
      "the contact number has not been updated" in {
        stubPatch("/update-contact-number", 406, Json.toJson(contactNumber).toString())
        val result = connector.updateContactNumber(contactNumber)
        await(result) shouldBe false
      }
    }
  }

}
