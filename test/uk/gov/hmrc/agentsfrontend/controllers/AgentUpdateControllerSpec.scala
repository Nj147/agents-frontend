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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.AgentUpdateConnector
import uk.gov.hmrc.agentsfrontend.views.html.UpdatePage

import scala.concurrent.Future

class AgentUpdateControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val updatePage = app.injector.instanceOf[UpdatePage]
  private val ac = mock(classOf[AgentUpdateConnector])
  private val fakeRequestWithARN = FakeRequest()
    .withSession( newSessions =
      "arn" -> "ARN2312",
      "businessName" -> "Business Ltd",
      "email" -> "john@gmail.com",
      "mobileNumber" -> "0798432443",
      "moc" -> "Text message, Phone",
      "Address" -> "12/SW255R3"
    )
  private val controller = new AgentUpdateController(Helpers.stubMessagesControllerComponents(), updatePage, ac)

  "agentUpdate" should {
    "return OK when session contains an arn" in {
      val result = controller.agentUpdate.apply(fakeRequestWithARN)
      status(result) shouldBe Status.OK
    }
    "Redirect to the dashboard when session does not contain an arn" in {
      val result = controller.agentUpdate.apply(FakeRequest().withSession())
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "agentUpdateSubmit" should {
    "return OK when update is successful" in {
      when(ac.updateAgent(any())) thenReturn Future.successful(true)
      val result = controller.agentUpdateSubmit.apply(fakeRequestWithARN)
      status(result) shouldBe Status.OK
    }
    "return BAD_REQUEST when update is unsuccessful" in {
      when(ac.updateAgent(any())) thenReturn Future.successful(false)
      val result = controller.agentUpdateSubmit.apply(fakeRequestWithARN)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }


}