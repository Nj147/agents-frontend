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

import org.jsoup.Jsoup
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status._
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, session, status}
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.controllers.{UpdateContactNumberController, routes}
import uk.gov.hmrc.agentsfrontend.views.html.ContactNumberPage

class UpdateContactNumberControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite{

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
  }

  "POST /update-contact" should {
    "return a bad request and a form with errors" when {
      "the user submits a form with an invalid contact number" in {
        when()
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "123456"))
        status(result) shouldBe BAD_REQUEST
        Jsoup.parse(contentAsString(result)).text() should include("Please enter a valid phone number")
      }
    }
    "redirect back to the dashboard [update summary] with updated session values" when {
      "a valid contact number is submitted" in {
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "01234567890"))
        status(result) shouldBe SEE_OTHER
        session(result).get("contactNumber") shouldBe Some("01234567890")
        redirectLocation(result) shouldBe Some(s"${routes.DashBoardController.index()}")
      }
    }
  }

}
