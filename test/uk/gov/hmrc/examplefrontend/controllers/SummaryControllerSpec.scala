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
import play.api.http.Status
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.examplefrontend.connectors.CustomerConnector
import uk.gov.hmrc.examplefrontend.views.html.{Confirmation, SummaryPage}
import scala.concurrent.{ExecutionContext, Future}

class SummaryControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite{
    override def fakeApplication(): Application =
      new GuiceApplicationBuilder()
        .build()

  val ec = mock(classOf[ExecutionContext])
  val cc = mock(classOf[CustomerConnector])

  private val fakeRequest = FakeRequest("GET", "/summary").withSession("names" -> "First/Last", "contact" -> "01234/me@gmail.com", "citizenship" -> "true", "dob" -> "03/04/1990")
  private val confirmation = app.injector.instanceOf[Confirmation]
  private val sum = app.injector.instanceOf[SummaryPage]

  val mockController = new SummaryController(Helpers.stubMessagesControllerComponents(), sum, confirmation, cc)

  private val controller = app.injector.instanceOf[SummaryController]
  "GET /summary" should {
      "return 200" in {
        val result = controller.pageStart(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        val result = controller.pageStart(fakeRequest)
        contentType(result) shouldBe Some("text/html")
        charset(result)     shouldBe Some("utf-8")
      }
      "display entered names" in {
        val result = controller.pageStart(fakeRequest)
        Jsoup.parse(contentAsString(result)).getElementById("fName").text shouldBe "First"
        Jsoup.parse(contentAsString(result)).getElementById("lName").text shouldBe "Last"
        Jsoup.parse(contentAsString(result)).getElementById("number").text shouldBe "1234"
        Jsoup.parse(contentAsString(result)).getElementById("email").text shouldBe "me@gmail.com"
        Jsoup.parse(contentAsString(result)).getElementById("citizen").text shouldBe "true"
        Jsoup.parse(contentAsString(result)).getElementById("dob").text shouldBe "03/04/1990"
      }
    }

  "GET /submit" should {
    "return 200" in {
      val result = controller.submit(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.submit(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
    "go to success page" in {
      when(cc.create(any())) thenReturn(Future.successful(true))
      val result = mockController.submit(fakeRequest)
      Jsoup.parse(contentAsString(result)).getElementById("result").text shouldBe "Customer Created!"
    }
    "go to failure page" in {
      when(cc.create(any())) thenReturn(Future.successful(false))
      val result = mockController.submit(fakeRequest)
      Jsoup.parse(contentAsString(result)).getElementById("result").text shouldBe "Customer Creation Failed"
    }
  }
 }



