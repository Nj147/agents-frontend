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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{defaultAwaitTimeout, session, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.AgentDetailsConnector
import uk.gov.hmrc.agentsfrontend.controllers.AgentDetailsController
import uk.gov.hmrc.agentsfrontend.models.AgentDetails
import uk.gov.hmrc.agentsfrontend.views.html.StartPage

import scala.concurrent.Future

class AgentDetailsControllersSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val connector = mock(classOf[AgentDetailsConnector])
  private val updatePage = app.injector.instanceOf[StartPage]
  private val controller = new AgentDetailsController(Helpers.stubMessagesControllerComponents(), connector, updatePage)

  def agent: AgentDetails = AgentDetails("ARN00000", "testBusinessName", "testEmail", 0x8, List("test"), "testAddressLine1", "testPostcode")

  "getUpdatePage" should {
    "redirect to update page" in {
      when(connector.getAgentDetails(any())) thenReturn (Future.successful(agent))
      val result = controller.getUpdatePage().apply(FakeRequest().withSession("arn" -> agent.arn))
      status(result) shouldBe Status.OK
      session(result).get("businessName").get shouldBe agent.businessName
      session(result).get("email").get shouldBe agent.email
      session(result).get("moc").get shouldBe "test"
      session(result).get("address").get shouldBe agent.propertyNumber +"/"+agent.postcode
    }
  }

}
