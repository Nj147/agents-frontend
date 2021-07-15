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

package uk.gov.hmrc.agentsfrontend.controllers.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status.{CONFLICT, NOT_FOUND, NO_CONTENT}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentsfrontend.connectors.InputClientCodeConnector
import uk.gov.hmrc.agentsfrontend.services.InputClientCodeService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InputClientCodeServiceISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {
  val connector: InputClientCodeConnector = mock(classOf[InputClientCodeConnector])
  val service = new InputClientCodeService(connector)

  "postClientCode" should {
    "return 204" when {
      "Agent code is successfully added" in {
        when(connector.postClientCode(any(), any())) thenReturn (Future(NO_CONTENT))
        val result = service.postClientCode("Agent", "Client")
        await(result) shouldBe NO_CONTENT
      }
    }
    "return 404" when {
      "Client code provided does not exist in Client database" in {
        when(connector.postClientCode(any(), any())) thenReturn (Future(NOT_FOUND))
        val result = service.postClientCode("Agent", "Error in Client Code")
        await(result) shouldBe NOT_FOUND
      }
    }
    "return 409" when {
      "Client already has an associated Agent" in {
        when(connector.postClientCode(any(), any())) thenReturn (Future(CONFLICT))
        val result = service.postClientCode("Agent", "Client code already in use")
        await(result) shouldBe CONFLICT
      }
    }
  }
}