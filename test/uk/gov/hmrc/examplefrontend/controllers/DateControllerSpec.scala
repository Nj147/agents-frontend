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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.guice.GuiceApplicationBuilder

class DateControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
    override def fakeApplication(): Application =
      new GuiceApplicationBuilder()
        .configure(
          "metrics.jvm"     -> false,
          "metrics.enabled" -> false
        )
        .build()

    private val fakeRequest = FakeRequest("GET", "/date")
    private val fakeRequestWithSession = FakeRequest("GET", "/date").withSession("dob" -> "01/01/2001")
    private val fakePostRequest = FakeRequest("POST", "/date").withFormUrlEncodedBody("day" -> "01",  "month" -> "01", "year" -> "2000")
    private val badFakePostRequest = FakeRequest("POST", "/date").withFormUrlEncodedBody("day" -> "", "month" -> "", "year" -> "")

    private val controller = app.injector.instanceOf[DateController]

    "GET /date" should {
      "return 200" in {
        val result = controller.pageStart(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        val result = controller.pageStart(fakeRequest)
        contentType(result) shouldBe Some("text/html")
        charset(result)     shouldBe Some("utf-8")
      }
      "fill in the text fields with session values" in {
        val result = controller.pageStart(fakeRequestWithSession)
        Jsoup.parse(contentAsString(result)).getElementById("day").`val`() shouldBe "1"
        Jsoup.parse(contentAsString(result)).getElementById("month").`val`() shouldBe "1"
        Jsoup.parse(contentAsString(result)).getElementById("year").`val`() shouldBe "2001"
      }
    }

  "POST /date" should {
    "add to the session data" in {
      val result = controller.processDate(fakePostRequest)
      session(result).get("dob").get shouldBe "1/1/2000"
    }
    "redirect to the next page" in {
      val result = controller.processDate(fakePostRequest)
      status(result) shouldBe 303
    }
    "return a bad request" in {
      val result = controller.processDate(badFakePostRequest)
      status(result) shouldBe 400
    }
  }
}
