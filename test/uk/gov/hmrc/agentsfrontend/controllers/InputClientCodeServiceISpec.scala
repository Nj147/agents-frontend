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

package uk.gov.hmrc.agentsfrontend.controllers

import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.agentsfrontend.connectors.InputClientCodeConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentClient
import uk.gov.hmrc.agentsfrontend.services.InputClientCodeService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InputClientCodeServiceISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {
  val connector: InputClientCodeConnector = mock(classOf[InputClientCodeConnector])
  val service = new InputClientCodeService(connector)

  "postClientCode" should {
    "return 204" when {
      "Agent code is successfully added" in {
        when(connector.postClientCode(any())) thenReturn (Future(204))
        val result = service.postClientCode(AgentClient("Agent", "Client"))
        await(result) shouldBe (204)
      }
    }
    "return 404" when {
      "Client code provided does not exist in Client database" in {
        when(connector.postClientCode(any())) thenReturn (Future(404))
        val result = service.postClientCode(AgentClient("Agent", "Error in Client Code"))
        await(result) shouldBe (404)
      }
    }
    "return 409" when {
      "Client already has an associated Agent" in {
        when(connector.postClientCode(any())) thenReturn (Future(409))
        val result = service.postClientCode(AgentClient("Agent", "Client code already in use"))
        await(result) shouldBe (409)
      }
    }
  }
}