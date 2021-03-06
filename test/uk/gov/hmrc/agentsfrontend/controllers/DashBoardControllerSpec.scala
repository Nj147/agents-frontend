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
import play.api.test.Helpers.{contentType, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.DashBoardConnector
import uk.gov.hmrc.agentsfrontend.controllers.predicates.LoginChecker
import uk.gov.hmrc.agentsfrontend.models.Client
import uk.gov.hmrc.agentsfrontend.views.html.Index
import scala.concurrent.Future

class DashBoardControllerSpec extends AnyWordSpec
  with Matchers
  with GuiceOneAppPerSuite {

  val dashBoard: Index = app.injector.instanceOf[Index]
  val connector: DashBoardConnector = mock(classOf[DashBoardConnector])
  val login: LoginChecker = app.injector.instanceOf[LoginChecker]
  val controller = new DashBoardController(Helpers.stubMessagesControllerComponents(), dashBoard, login, connector)

  val obj: Client = Client("CRN684077E0",
    "testName",
    "testBusiness",
    "testContact",
    "12",
    "testPostcode",
    "testBusinessType",
    "testArn")

  "DashBoardController index" should {
    "return 200 Ok" in {
      when(connector.getAllClientsData(any())) thenReturn (Future.successful(Option(List(obj))))
      val result = controller
        .index()
        .apply(FakeRequest("GET", "/dashboard")
          .withSession("arn" -> "arn"))
      contentType(result) shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
      status(result) shouldBe 200
    }
  }

}
