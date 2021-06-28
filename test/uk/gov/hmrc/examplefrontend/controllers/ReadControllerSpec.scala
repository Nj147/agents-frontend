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
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._
import uk.gov.hmrc.examplefrontend.connectors.CustomerConnector
import uk.gov.hmrc.examplefrontend.persistence.domain.Customer
import uk.gov.hmrc.examplefrontend.views.html.{Read, ReadAll, ReadOne}

import scala.concurrent.Future

class ReadControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/read")
  private val fakePostRequest = FakeRequest("POST", "/readOne").withFormUrlEncodedBody("email" -> "test@test.com")
  private val con = mock(classOf[CustomerConnector])
  private val readOne = app.injector.instanceOf[ReadOne]
  private val read = app.injector.instanceOf[Read]
  private val readAll = app.injector.instanceOf[ReadAll]
  private val controller = new ReadController(Helpers.stubMessagesControllerComponents(), readAll, read, readOne, con)

  private val cust1 = Customer("First", "Last", 123456789, "test1@test.com", false, "01/01/2000")
  private val cust2 = Customer("FirstName", "LastName", 987654321, "test2@test.com", false, "31/12/1999")

  "GET /read" should {
    "return 200" in {
      val result = controller.read().apply(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller.read().apply(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "GET /readAll" should {
    "display a series of tables with customer information displayed" in {
      when(con.readAll()) thenReturn(Future.successful(Seq(cust1, cust2)))
      val result = controller.readAll().apply(fakeRequest)
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-table").size() shouldBe 2
    }
  }

  "GET /readOne" should {
    "display a table with customer information displayed" in {
      when(con.readOne(any())) thenReturn(Future.successful(Some(cust1)))
      val result = controller.readOne().apply(fakePostRequest)
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-table").size() shouldBe 1
    }
    "return to the email entry with a bad request and an error saying email did not match the database" in {
      when(con.readOne(any())) thenReturn(Future.successful(null))
      val result = controller.readOne().apply(fakePostRequest)
      status(result) shouldBe 400
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-table").size() shouldBe 0
      Jsoup.parse(contentAsString(result)).text() should include("Value entered does not match an existing email")
    }
    "return to the email entry with a bad request and an error saying email was not valid" in {
      val result = controller.readOne().apply(FakeRequest("POST", "/readOne").withFormUrlEncodedBody("email" -> ""))
      status(result) shouldBe 400
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-table").size() shouldBe 0
      Jsoup.parse(contentAsString(result)).text() should include("Value entered is not a valid email")
    }
  }
}
