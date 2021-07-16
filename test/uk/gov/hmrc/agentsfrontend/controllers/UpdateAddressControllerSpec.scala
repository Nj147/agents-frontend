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
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, redirectLocation, session, status}
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.views.html.AddressPage

import scala.concurrent.Future

class UpdateAddressControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite{

  val conn: UpdateConnector = mock(classOf[UpdateConnector])
  val addressPage: AddressPage = app.injector.instanceOf[AddressPage]
  val controller: UpdateAddressController = new UpdateAddressController(Helpers.stubMessagesControllerComponents(), addressPage ,conn)
  private val fakeRequest = FakeRequest("/GET", "/update-contact")
  private val fakePostRequest = FakeRequest("/POST", "/update-contact")

  "GET /update-address" should {
    "return status 200 with a contact number form field" when {
      "the user is logged in as an agent" in {
        val result = controller.startPage.apply(fakeRequest.withSession("arn" -> "ARN0000001"))
        status(result) shouldBe OK
        contentType(result) shouldBe Some("text/html")
        Helpers.charset(result) shouldBe Some("utf-8")
        Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-10").size shouldBe 2
      }
    }
    "redirect to the home page" when {
      "an agent is not logged in" in {
        val result = controller.startPage.apply(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
  }

  "POST /update-address" should {
    "return a bad request and a form with errors" when {
      "the user submits a form with invalid value(s) in either field" in {
        val result = controller.processAddress.apply(fakePostRequest.withFormUrlEncodedBody("propertyNumber" -> "", "postcode" -> "asdadsasdasdasd").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe BAD_REQUEST
        Jsoup.parse(contentAsString(result)).text() should include("Please enter the registered postcode of your business, for example - 'HA8 3NY'")
      }
      "the entered changes are not accepted by the database" in {
        when(conn.updateAddress(any(), any())) thenReturn Future.successful(false)
        val result = controller.processAddress.apply(fakePostRequest.withFormUrlEncodedBody("propertyNumber" -> "1 New Street", "postcode" -> "AA12 1AB").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe BAD_REQUEST
        Jsoup.parse(contentAsString(result)).text() should include("Change of details could not be process, please try again")
      }
    }
    "redirect" when {
      "a valid contact number is submitted and the backend accepts the changes" in {
        when(conn.updateAddress(any(), any())) thenReturn Future.successful(true)
        val result = controller.processAddress.apply(fakePostRequest.withFormUrlEncodedBody("propertyNumber" -> "1 New Street", "postcode" -> "AA12 1AB").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe ("/agents-frontend/update-page")
      }
      "the client is not logged in and tries to access the page" in {
        val result = controller.processAddress.apply(fakePostRequest.withFormUrlEncodedBody("propertyNumber" -> "1 New Street", "postcode" -> "AA12 1AB"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/agents-frontend/start-page"
      }
    }
  }

}
