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

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.ClientConnector
import uk.gov.hmrc.agentsfrontend.controllers.RemoveClientController
import uk.gov.hmrc.agentsfrontend.views.html.{RemovalConfirmation, RemoveClients}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RemoveClientControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val fakeRequest = FakeRequest("GET", "/")

  private val conn = mock(classOf[ClientConnector])
  private val removePage = app.injector.instanceOf[RemoveClients]
  private val resultConf = app.injector.instanceOf[RemovalConfirmation]
  private val controller = new RemoveClientController(Helpers.stubMessagesControllerComponents(), removePage, resultConf, conn)

  "GET /removeClients/CRN0001" should {
    "return 200" in {
      val result = controller.removeClients("CRN0001")(fakeRequest.withSession("arn" -> "ARN01234567"))
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
    "return HTML" in {
      val result = controller.removeClients("CRN0001")(fakeRequest)
      redirectLocation(result).get shouldBe ("/agents-frontend/agent-login")
    }
  }

  "GET /processRemoval/CRN0001" should {
    "return 200" in {
      when(conn.removeClient(any(), any())) thenReturn(Future.successful(true))
      val result = controller.processRemoval("CRN0001")(fakeRequest.withSession("arn" -> "ARN01234567"))
      status(result) shouldBe Status.OK
    }
    "return HTML" in {
      when(conn.removeClient(any(), any())) thenReturn(Future.successful(true))
      val result = controller.processRemoval("CRN0001")(fakeRequest.withSession("arn" -> "ARN01234567"))
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
    "have a success message" in {
      when(conn.removeClient(any(), any())) thenReturn(Future.successful(true))
      val result = controller.processRemoval("CRN0001")(fakeRequest.withSession("arn" -> "ARN01234567"))
      Jsoup.parse(contentAsString(result)).text() should include("Client has been successfully unlinked from your account")
      Jsoup.parse(contentAsString(result)).text() should not include ("Client has failed to be unlinked from your account")
    }
    "have a failure message" in {
      when(conn.removeClient(any(), any())) thenReturn(Future.successful(false))
      val result = controller.processRemoval("CRN0001")(fakeRequest.withSession("arn" -> "ARN01234567"))
      Jsoup.parse(contentAsString(result)).text() should include("Client has failed to be unlinked from your account")
      Jsoup.parse(contentAsString(result)).text() should not include ("Client has been successfully unlinked from your account")
    }
  }
}