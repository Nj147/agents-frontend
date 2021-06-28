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
import play.api.test.Helpers.{charset, contentAsString, contentType, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.examplefrontend.connectors.CustomerConnector
import uk.gov.hmrc.examplefrontend.views.html.{DeleteResult, DeleteStart}

import scala.concurrent.Future

class DeleteControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/update")
  private val con = mock(classOf[CustomerConnector])
  private val deleteStart = app.injector.instanceOf[DeleteStart]
  private val deleteResult = app.injector.instanceOf[DeleteResult]
  private val controller = new DeleteController(Helpers.stubMessagesControllerComponents(), deleteStart, deleteResult, con)

  "GET /delete" should {
    "return 200" in {
      val result = controller.startDelete.apply(fakeRequest)
      status(result) shouldBe OK
    }
    "return HTML with a form containing one field" in {
      val result = controller.startDelete().apply(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
      Jsoup.parse(contentAsString(result)).title() shouldBe "Account Deletion"
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-20").size() shouldBe 1
    }
  }

  "POST /delete" should {
    "return a bad request and a form with error back to the email page" in {
      val result = controller.processDelete().apply(FakeRequest("POST", "/delete").withFormUrlEncodedBody("email" -> ""))
      status(result) shouldBe BAD_REQUEST
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-20").size() shouldBe 1
      Jsoup.parse(contentAsString(result)).text() should include("Entered value is not valid email")
      Jsoup.parse(contentAsString(result)).title() shouldBe "Account Deletion"
    }
    "load a new page with confirmation saying the customer has been deleted" in {
      when(con.delete(any())) thenReturn(Future.successful(true))
      val result = controller.processDelete().apply(FakeRequest("POST", "/update").withFormUrlEncodedBody("email" -> "test@test.com"))
      status(result) shouldBe OK
      Jsoup.parse(contentAsString(result)).title() shouldBe "Delete Result"
      Jsoup.parse(contentAsString(result)).getElementById("result").text() should include("Customer Deleted!")
    }
    "load a new page with a message saying the customer has not been deleted" in {
      when(con.delete(any())) thenReturn(Future.successful(false))
      val result = controller.processDelete().apply(FakeRequest("POST", "/update").withFormUrlEncodedBody("email" -> "test@test.com"))
      status(result) shouldBe OK
      Jsoup.parse(contentAsString(result)).title() shouldBe "Delete Result"
      Jsoup.parse(contentAsString(result)).getElementById("result").text() should include("Customer Deleting Failed")

    }
  }
}
