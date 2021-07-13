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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, session, status}

class UpdateAddressControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite{

  val controller: UpdateAddressController = app.injector.instanceOf[UpdateAddressController]
  private val fakeRequest = FakeRequest("/GET", "/update-contact")
  private val fakePostRequest = FakeRequest("/POST", "/update-contact")

  "GET /update-address" should {
    "return status 200 with a contact number form field" when {
      "the user is logged in as an agent" in {
        val result = controller.startPage.apply(fakeRequest.withSession("arn" -> "ARN0000001"))
        status(result) shouldBe OK
        Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-10").size shouldBe 2
      }
    }
    "redirect to the home page" when {
      "an agent is not logged in" in {
        val result = controller.startPage.apply(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
    "have prefilled values in the form" when {
      "the logged in user opens the page" in {
        val result = controller.startPage.apply(fakeRequest.withSession("arn" -> "ARN0000001", "address" -> "1 New Street/AA12 1AB"))
        Jsoup.parse(contentAsString(result)).getElementById("propertyNumber").`val`() should be("1 New Street")
        Jsoup.parse(contentAsString(result)).getElementById("postcode").`val`() should be("AA12 1AB")
      }
    }
  }

  "POST /update-address" should {
    "return a bad request and a form with errors" when {
      "the user submits a form with an invalid contact number" in {
        val result = controller.processAddress.apply(fakePostRequest.withFormUrlEncodedBody("propertyNumber" -> "1 New Street", "postcode" -> "asdadsasdasdasd"))
        status(result) shouldBe BAD_REQUEST
        Jsoup.parse(contentAsString(result)).text() should include("Input is not a valid postcode")
      }
    }
    "redirect back to the dashboard [update summary] with updated session values" when {
      "a valid contact number is submitted" in {
        val result = controller.processAddress.apply(fakePostRequest.withFormUrlEncodedBody("propertyNumber" -> "1 New Street", "postcode" -> "AA12 1AB"))
        status(result) shouldBe SEE_OTHER
        session(result).get("address") shouldBe Some("1 New Street/AA12 1AB")
        redirectLocation(result) shouldBe Some(s"${routes.DashBoardController.index()}")
      }
    }
  }

}
