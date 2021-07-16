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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.controllers.UpdateController
import org.mockito.Mockito.{mock, when}
import play.api.http.Status
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.agentsfrontend.connectors.AgentDetailsConnector
import uk.gov.hmrc.agentsfrontend.models.AgentDetails
import uk.gov.hmrc.agentsfrontend.views.html.UpdatePage

import scala.concurrent.Future

class UpdateControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val ac = mock(classOf[AgentDetailsConnector])
  private val updatePage = app.injector.instanceOf[UpdatePage]
  private val controller = new UpdateController(Helpers.stubMessagesControllerComponents(), ac, updatePage)
  private val agentDetails = AgentDetails("ARN34234", "Business Ltd", "email@email.com", "073253423".toLong, Seq("Text message"), "12", "SW12 R4T")

  "/update-address" should {
    "return an OK status" when {
      "a valid arn is submitted" in {
        when(ac.getAgentDetails(any())) thenReturn Future.successful(Some(agentDetails))
        val result = controller.getDetails.apply(FakeRequest().withSession("arn" -> "ARN43242334"))
        status(result) shouldBe Status.OK
      }
    }
    "redirect to login" when {
      "no arn is found" in {
        when(ac.getAgentDetails(any())) thenReturn Future.successful(Some(agentDetails))
        val result = controller.getDetails.apply(FakeRequest())
        status(result) shouldBe Status.SEE_OTHER
      }
    }
    "return an internal server error" when {
      "the service fails to provide agent details" in {
        when(ac.getAgentDetails(any())) thenReturn Future.successful(None)
        val result = controller.getDetails.apply(FakeRequest().withSession("arn" -> "ARN43242334"))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
