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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{defaultAwaitTimeout, session, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.AgentConnector
import uk.gov.hmrc.agentsfrontend.views.html.AgentLoginPage

import scala.concurrent.Future

class AgentLoginControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val ac = mock(classOf[AgentConnector])
  private val agentLoginPage = app.injector.instanceOf[AgentLoginPage]
  private val controller = new AgentLoginController(Helpers.stubMessagesControllerComponents(), agentLoginPage, ac)

  "agentLogin" should {
    "return status 200 with an empty session" when {
      "agent login page is loaded" in {
        val result = controller.agentLogin.apply(FakeRequest())
        status(result) shouldBe Status.OK
      }
    }
    "redirect to the Dashboard" when {
      "an agent is already logged in" in {
        val result = controller.agentLogin.apply(FakeRequest().withSession("arn" -> "G4G3G4FSV"))
        status(result) shouldBe Status.SEE_OTHER
      }
    }
  }

  "agentLoginSubmit" should {
    "return bad request if some fields are blank" in {
      val result = controller.agentLoginSubmit.apply(FakeRequest().withFormUrlEncodedBody("arn" -> "", "password" -> "pa55w0rd"))
      status(result) shouldBe Status.BAD_REQUEST
    }
    "redirect if all fields filled in" in {
      when(ac.checkLogin(any())) thenReturn Future.successful(true)
      val result = controller.agentLoginSubmit.apply(FakeRequest().withFormUrlEncodedBody("arn" -> "F34FF34", "password" -> "pa55w0rd"))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return a status NOT_FOUND" in {
      when(ac.checkLogin(any())) thenReturn Future.successful(false)
      val result = controller.agentLoginSubmit.apply(FakeRequest().withFormUrlEncodedBody("arn" -> "F34FF34", "password" -> "pa55w0rd"))
      status(result) shouldBe Status.NOT_FOUND
    }
  }

  "logout" should {
    "redirect if all fields filled in" in {
      val result = controller.logout.apply(FakeRequest())
      status(result) shouldBe Status.SEE_OTHER
      session(result) shouldBe empty
    }
  }
}
