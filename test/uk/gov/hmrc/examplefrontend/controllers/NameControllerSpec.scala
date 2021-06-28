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

class NameControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/name")
  private val fakeRequestWithSession = FakeRequest("GET", "/name").withSession("names" -> "John/Smith")
  private val fakePostRequest = FakeRequest("POST", "/name").withFormUrlEncodedBody("firstName" -> "John",  "lastName" -> "Smith")
  private val badFakePostRequest = FakeRequest("POST", "/name").withFormUrlEncodedBody("firstName" -> "", "lastName" -> "Smith")
  private val controller = app.injector.instanceOf[NameController]

  "GET /name" should {
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
      Jsoup.parse(contentAsString(result)).getElementById("firstName").`val`() shouldBe "John"
      Jsoup.parse(contentAsString(result)).getElementById("lastName").`val`() shouldBe "Smith"
    }
  }

  "POST /name" should {
    "add to session data" in {
      val result = controller.processName(fakePostRequest)
      session(result).get("names").get shouldBe "John/Smith"
    }
    "redirect to the next page" in {
      val result = controller.processName(fakePostRequest)
      status(result) shouldBe 303
    }
    "return a bad request" in {
      val result = controller.processName(badFakePostRequest)
      status(result) shouldBe 400
    }
  }
}

