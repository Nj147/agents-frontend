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
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.agentsfrontend.connectors.ClientConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentClient

class ClientConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach{
  lazy val connector: ClientConnector = injector.instanceOf[ClientConnector]

  override def beforeEach() = startWireMock()

  override def afterEach() = stopWireMock()

  "POST /removeClient" should {
    "return true when accepted response returned" in {
      stubPost("/removeClient",204, "")
      val result = connector.removeClient(AgentClient("ARN01234567", "CRN98765432"))
      await(result) shouldBe true
    }
    "return false when bad request response returned" in {
      stubPost("/removeClient",400, "")
      val result = connector.removeClient(AgentClient("ARN01234567", "CRN98765432"))
      await(result) shouldBe false
    }
  }
}
