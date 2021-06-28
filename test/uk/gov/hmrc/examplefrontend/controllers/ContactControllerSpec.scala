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

class ContactControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/contact")
  private val fakeRequestWithSession = FakeRequest("GET", "/contact").withSession("contact" -> "1234567/me@gmail.com")
  private val fakePostRequest = FakeRequest("POST", "/contact").withFormUrlEncodedBody("email" -> "me@gmail.com",  "phoneNumber" -> "015987")
  private val badFakePostRequest = FakeRequest("POST", "/contact").withFormUrlEncodedBody("email" -> "", "phoneNumber" -> "")

  private val controller = app.injector.instanceOf[ContactController]

  "GET /contact" should {
    "return 200" in {
      val result = controller.pageStart(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.pageStart(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "fill the text fields if session data is present" in {
      val result = controller.pageStart(fakeRequestWithSession)
      Jsoup.parse(contentAsString(result)).getElementById("phoneNumber").`val`() shouldBe "1234567"
      Jsoup.parse(contentAsString(result)).getElementById("email").`val`() shouldBe "me@gmail.com"
    }
  }

  "POST /contact" should {
    "add to the session data" in {
      val result = controller.processContact(fakePostRequest)
      session(result).get("contact").get shouldBe "15987/me@gmail.com"
    }
    "redirect to the next page" in {
      val result = controller.processContact(fakePostRequest)
      status(result) shouldBe 303
    }
    "return a bad request" in {
      val result = controller.processContact(badFakePostRequest)
      status(result) shouldBe 400
    }
  }
}

