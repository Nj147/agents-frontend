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

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.InputClientCodeConnector

class InputClientCodeConnectorISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach {
  lazy val connector: InputClientCodeConnector = injector.instanceOf[InputClientCodeConnector]

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  "postClientCode" should {
    "return 204" when {
      "Agent code is successfully added" in {
        stubPatch("/clients/CRN000001/add", 204, "")
        val result = connector.postClientCode("ARN00001", "CRN000001")
        await(result) shouldBe (204)
      }
    }
    "return 404" when {
      "Client code provided does not exist in Client database" in {
        stubPatch("/clients/CRN000001/add", 404, "")
        val result = connector.postClientCode("ARN00001", "CRN000001")
        await(result) shouldBe (404)
      }
    }
    "return 409" when {
      "Client already has an associated Agent" in {
        stubPatch("/clients/CRN000001/add", 409, "")
        val result = connector.postClientCode("ARN00001", "CRN000001")
        await(result) shouldBe (409)
      }
    }
  }
}
