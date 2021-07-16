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
import play.api.http.Status._
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.controllers.UpdateContactNumberController
import uk.gov.hmrc.agentsfrontend.views.html.ContactNumberPage
import scala.concurrent.Future

class UpdateContactNumberControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val connector: UpdateConnector = mock(classOf[UpdateConnector])
  val updatePage: ContactNumberPage = app.injector.instanceOf[ContactNumberPage]
  val controller = new UpdateContactNumberController(Helpers.stubMessagesControllerComponents(), updatePage, connector)
  private val fakeRequest = FakeRequest("/GET", "/update-contact")
  private val fakePostRequest = FakeRequest("/POST", "/update-contact")

  "GET /update-contact" should {
    "return status 200" when {
      "the user is logged in as an agent and it loads the form" in {
        val result = controller.startPage.apply(fakeRequest.withSession("arn" -> "ARN0000001"))
        status(result) shouldBe OK
        Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-10").size shouldBe 1
      }
    }
    "redirect to the home page" when {
      "an agent is not logged in" in {
        val result = controller.startPage.apply(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
  }

  "POST /update-contact" should {
    "returns BadRequest" when {
      "an empty form is submitted" in {
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe BAD_REQUEST
      }
      "the database doesn't accept the change" in {
        when(connector.updateContactNumber(any(), any())) thenReturn (Future.successful(false))
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "07986526663").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "returns see other" when {
      "there is no session variable set" in {
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("" -> ""))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe ("/agents-frontend/agent-login")
      }
      "the change has been accepted" in {
        when(connector.updateContactNumber(any(), any())) thenReturn (Future.successful(true))
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "09876509876").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe ("/agents-frontend/update-page")
      }
    }
  }

}
