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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.controllers.UpdateCorrespondenceController

import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.agentsfrontend.views.html.UpdateCorrespondencePage


class UpdateCorrespondenceControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")
  val updateMOC: UpdateCorrespondencePage = app.injector.instanceOf[UpdateCorrespondencePage]
  val controller = new UpdateCorrespondenceController(Helpers.stubMessagesControllerComponents(), updateMOC)

  "GET /update-correspondence " should {
    "return 200" in {
      val result = controller.getCorrespondence(fakeRequest)
      status(result) shouldBe Status.OK
    }
    "return HTML" in {
      val result = controller.getCorrespondence(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }
    "return a page with 1 input" in {
      val result = controller.getCorrespondence(fakeRequest)
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-checkboxes__item").size shouldBe 4
    }
  }

  "POST /update-correspondence " should {
    "return 303 when redirected to dashboard" in {
      val result = controller.updateCorrespondence().apply(FakeRequest("POST", "/updateCorrespondence").withFormUrlEncodedBody("modes[]" -> "call"))
      status(result) shouldBe Status.SEE_OTHER
    }

    "return 400 when the nothing is sent" in {
      val result = controller.updateCorrespondence().apply(FakeRequest("POST", "/updateCorrespondence").withFormUrlEncodedBody())
      status(result) shouldBe Status.BAD_REQUEST
    }

  }
}
