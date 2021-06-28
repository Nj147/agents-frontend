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

package uk.gov.hmrc.examplefrontend.controllers

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.EssentialAction
import play.api.test.Helpers.{charset, contentAsString, contentType, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.examplefrontend.connectors.CustomerConnector
import uk.gov.hmrc.examplefrontend.persistence.domain.{Customer, DateOfBirth}
import uk.gov.hmrc.examplefrontend.views.html.{Update, UpdatePage, UpdateResult}

import scala.concurrent.Future

class UpdateControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/update")
  private val con = mock(classOf[CustomerConnector])
  private val update = app.injector.instanceOf[Update]
  private val updatePage = app.injector.instanceOf[UpdatePage]
  private val updateResult = app.injector.instanceOf[UpdateResult]
  private val controller = new UpdateController(Helpers.stubMessagesControllerComponents(), update, updatePage, updateResult, con)

  private val cust1 = Customer("First", "Last", 123456789, "test1@test.com", false, "01/01/2000")
  private val cust2 = Customer("FirstName", "LastName", 987654321, "test2@test.com", false, "31/12/1999")

  "GET /update" should {
    "return 200" in {
      val result = controller.updateStart().apply(fakeRequest)
      status(result) shouldBe OK
    }
    "return HTML with a form containing one field" in {
      val result = controller.updateStart().apply(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      Jsoup.parse(contentAsString(result)).title() shouldBe "Account Login"
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-20").size() shouldBe 1
    }
  }

  "POST /update" should {
    "send a bad request containing a form with errors back to the email page" in {
      val result = controller.processEmail().apply(FakeRequest("POST", "/update").withFormUrlEncodedBody("email" -> ""))
      status(result) shouldBe BAD_REQUEST
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-20").size() shouldBe 1
      Jsoup.parse(contentAsString(result)).text() should include("Value entered is not a valid email")
    }
    "load a new page with fields for all customer details" in {
      when(con.readOne(any())) thenReturn(Future.successful(Some(cust1)))
      val result = controller.processEmail().apply(FakeRequest("POST", "/update").withFormUrlEncodedBody("email" -> "test@test.com"))
      status(result) shouldBe OK
      Jsoup.parse(contentAsString(result)).title() shouldBe "Update"
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-label--m").size() shouldBe 6
      Jsoup.parse(contentAsString(result)).getElementById("firstName").`val`() shouldBe "First"
      Jsoup.parse(contentAsString(result)).getElementById("lastName").`val`() shouldBe "Last"
      Jsoup.parse(contentAsString(result)).getElementById("phoneNumber").`val`() shouldBe "123456789"
      Jsoup.parse(contentAsString(result)).getElementById("day").`val`() shouldBe "1"
      Jsoup.parse(contentAsString(result)).getElementById("month").`val`() shouldBe "1"
      Jsoup.parse(contentAsString(result)).getElementById("year").`val`() shouldBe "2000"

    }
    "send a bad request with a error message saying the email doesn't match the database" in {
      when(con.readOne(any())) thenReturn(Future.successful(null))
      val result = controller.processEmail().apply(FakeRequest("POST", "/update").withFormUrlEncodedBody("email" -> "test1@test.com"))
      status(result) shouldBe BAD_REQUEST
      Jsoup.parse(contentAsString(result)).title() shouldBe "Account Login"
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-label--m").size() shouldBe 1
    }
  }

  "POST /updateProcess" should {
    "return a bad request and form with errors" in {
      val result = controller.processUpdate.apply(FakeRequest("POST", "/updateProcess").withFormUrlEncodedBody("firstName" -> "", "lastName" -> "Last", "phoneNumber" -> "987654321" ,"britishCitizen" -> "false", "dob" -> "31/12/1999"))
      status(result) shouldBe 400
      Jsoup.parse(contentAsString(result)).title() shouldBe "Update"
    }
    "return an Ok response and confirmation that customer has been updated" in {
      when(con.update(any())) thenReturn(Future.successful(true))
      val result = controller.processUpdate.apply(FakeRequest("POST", "/updateProcess").withFormUrlEncodedBody("firstName" -> "First", "lastName" -> "Last", "phoneNumber" -> "987654321" ,"britishCitizen" -> "false", "dob.day" -> "04", "dob.month" -> "05", "dob.year" -> "1998").withSession("email" -> "test@test.com"))
      status(result) shouldBe 200
      Jsoup.parse(contentAsString(result)).title() shouldBe "Update Result"
      Jsoup.parse(contentAsString(result)).getElementById("result").text() should include("Customer Updated")
    }
    "return an Ok response but a message that customer has failed to be updated" in {
      when(con.update(any())) thenReturn(Future.successful(false))
      val result = controller.processUpdate.apply(FakeRequest("POST", "/updateProcess").withFormUrlEncodedBody("firstName" -> "First", "lastName" -> "Last", "phoneNumber" -> "987654321" ,"britishCitizen" -> "false", "dob.day" -> "04", "dob.month" -> "05", "dob.year" -> "1998").withSession("email" -> "test@test.com"))
      status(result) shouldBe 200
      Jsoup.parse(contentAsString(result)).title() shouldBe "Update Result"
      Jsoup.parse(contentAsString(result)).getElementById("result").text() should include("Customer Updating Failed")
    }
  }

}