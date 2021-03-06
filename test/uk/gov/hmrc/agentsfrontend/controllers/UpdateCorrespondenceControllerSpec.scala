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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{contentType, defaultAwaitTimeout, redirectLocation, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.controllers.predicates.LoginChecker
import uk.gov.hmrc.agentsfrontend.views.html.UpdateCorrespondencePage
import scala.concurrent.Future

class UpdateCorrespondenceControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  val connector: UpdateConnector = mock(classOf[UpdateConnector])
  private val fakeRequest = FakeRequest("GET", "/").withSession("arn" -> "09876543")
  private val fakePostRequest = FakeRequest("/POST", "/update-correspondence")
  val updateMOC: UpdateCorrespondencePage = app.injector.instanceOf[UpdateCorrespondencePage]
  val login: LoginChecker = app.injector.instanceOf[LoginChecker]
  val controller = new UpdateCorrespondenceController(Helpers.stubMessagesControllerComponents(), connector, login, updateMOC)

  "GET /update-correspondence " should {
    "return HTML" in {
      val result = controller.getCorrespondence(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }
  }

  "POST /update-correspondence " should {
    "returns BadRequest" when {
      "an empty form is submitted" in {
        val result = controller.updateCorrespondence().apply(fakePostRequest.withFormUrlEncodedBody().withSession("arn" -> "ARN0000001"))
        status(result) shouldBe BAD_REQUEST
      }
      "the database doesn't accept the change" in {
        when(connector.updateCorrespondence(any(), any())) thenReturn (Future.successful(false))
        val result = controller.updateCorrespondence().apply(fakePostRequest.withFormUrlEncodedBody("modes[]" -> "text").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "returns see other" when {
      "the change has been accepted" in {
        when(connector.updateCorrespondence(any(), any())) thenReturn (Future.successful(true))
        val result = controller.updateCorrespondence().apply(fakePostRequest.withFormUrlEncodedBody("modes[]" -> "text").withSession("arn" -> "ARN0000001"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe ("/agents-frontend/update-page")
      }
    }
  }

}
