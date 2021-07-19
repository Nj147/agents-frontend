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
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.views.html.UpdateEmailPage
import scala.concurrent.Future
class UpdateEmailControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()
  private val uc = mock(classOf[UpdateConnector])
  private val fakeRequest = FakeRequest("GET", "/update-email")
  private val fakePostRequest = FakeRequest("POST", "/update-email")
  private val updateEmailPage = app.injector.instanceOf[UpdateEmailPage]
  private val controller = new UpdateEmailController(Helpers.stubMessagesControllerComponents(), updateEmailPage, uc)
  "GET/update-email" should {
    "return status 200 with a email address form field" when {
      "the user is logged in as an agent" in {
        when(uc.updateEmail(any(), any())) thenReturn Future.successful(true)
        val result = controller.displayUpdateEmailPage().apply(fakeRequest.withSession("arn" -> "ARN0002034"))
        status(result) shouldBe OK
        Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-form-group").size shouldBe 2
      }
    }
    "redirect to the home page" when {
      "an agent is not logged in" in {
        val result = controller.displayUpdateEmailPage().apply(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
  }
  "POST/update-email" should {
    "return a bad request and the form with error " when {
      "the agent submits a form with an invalid email address" in {
        val result = controller.processUpdateEmail().apply(fakePostRequest.withFormUrlEncodedBody("email" -> "test.test").withSession("arn" -> "ARN0001"))
        status(result) shouldBe BAD_REQUEST
        Jsoup.parse(contentAsString(result)).text() should include("Please enter a valid email address")
      }
    }
    "redirect back to the dashboard with updated session email value" when {
      "a valid email address is submitted" in {
        when(uc.updateEmail(any(), any())) thenReturn Future.successful(true)
        val result = controller.processUpdateEmail().apply(fakePostRequest.withFormUrlEncodedBody("email" -> "test@test.com").withSession("arn" -> "ARN0001"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some("/agents-frontend/update-page")
      }
    }
    "return bad request if data base return false" when {
      "a valid email address is submitted" in {
        when(uc.updateEmail(any(), any())) thenReturn Future.successful(false)
        val result = controller.processUpdateEmail().apply(fakePostRequest.withFormUrlEncodedBody("email" -> "test@test.com").withSession("arn" -> "ARN0001"))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "redirect to login page" when {
      "arn is not in session" in {
        val result = controller.processUpdateEmail().apply(fakePostRequest.withFormUrlEncodedBody("email" -> "test@test.com"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some("/agents-frontend/agent-login")
      }
    }
  }

}