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
import uk.gov.hmrc.agentsfrontend.services.InputClientCodeService
import uk.gov.hmrc.agentsfrontend.views.html.InputClientCode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json.{JsObject, Json}

class InputClientCodeControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")
  val clientCode: InputClientCode = app.injector.instanceOf[InputClientCode]
  val service: InputClientCodeService = mock(classOf[InputClientCodeService])
  val controller = new InputClientCodeController(Helpers.stubMessagesControllerComponents(), clientCode, service)
  val obj: JsObject = Json.obj("arn" -> "agent", "crn" -> "client")

  "GET /clientCode" should {
    "return 200" in {
      val result = controller.getInputClientCode(fakeRequest.withSession("arn" -> "ARN0001"))
      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }
    "return 303" in {
      val result = controller.getInputClientCode(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should include("/agent-login")
    }
  }

  "submitClientCode" should {
    "return 204 when successfully added agent to client and redirected to success page" in {
      when(service.postClientCode(any(), any())) thenReturn (Future.successful(204))
      val result = controller.submitClientCode().apply(FakeRequest("POST", "/clientCode").withSession("arn" -> "agent").withFormUrlEncodedBody("crn" -> "client"))
      status(result) shouldBe SEE_OTHER
    }
    "return 404 when the client code provided does not exist in the client database" in {
      when(service.postClientCode(any(), any())) thenReturn (Future.successful(404))
      val result = controller.submitClientCode().apply(FakeRequest("POST", "/clientCode").withSession("arn" -> "agent").withFormUrlEncodedBody("crn" -> "client"))
      status(result) shouldBe NOT_FOUND
    }
    "return 409 when the client code provided already has an agent linked with them" in {
      when(service.postClientCode(any(), any())) thenReturn (Future.successful(409))
      val result = controller.submitClientCode().apply(FakeRequest("POST", "/clientCode").withSession("arn" -> "agent").withFormUrlEncodedBody("crn" -> "client"))
      status(result) shouldBe CONFLICT
    }
    "return 400 when the nothing is sent" in {
      val result = controller.submitClientCode().apply(FakeRequest("POST", "/clientCode").withSession("arn" -> "agent").withFormUrlEncodedBody())
      status(result) shouldBe BAD_REQUEST
    }
    "returns 303 if the client service is down" in {
      val result = controller.submitClientCode().apply(FakeRequest("POST", "clientCode").withFormUrlEncodedBody("crn" -> "client"))
      status(result) shouldBe SEE_OTHER
    }
    "returns 500 if the backend service fails to respond" in {
      when(service.postClientCode(any(), any())) thenReturn (Future.successful(500))
      val result = controller.submitClientCode().apply(FakeRequest("POST", "clientCode").withSession("arn" -> "agent").withFormUrlEncodedBody("crn" -> "client"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

  }
}

