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
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.models.Client
import uk.gov.hmrc.agentsfrontend.services.DashBoardClientService
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.agentsfrontend.views.html.Index
import scala.concurrent.Future

class DashBoardControllerSpec  extends AnyWordSpec with Matchers with GuiceOneAppPerSuite{
   val  dashBoard = app.injector.instanceOf[Index]
   val clientService:DashBoardClientService = mock(classOf[DashBoardClientService])
   val controller = new DashBoardController(Helpers.stubMessagesControllerComponents(),dashBoard, clientService)

   val obj = Client("CRN684077E0",
                    "testName",
                    "testBusiness",
                     "testContact",
                     12,
                     "testPostcode",
                     "testBusinessType",
                     "testArn" )

  "DashBoardController index" should{
    "return 200 Ok" in {
      when(clientService.processAllClientsData(any())) thenReturn(Future.successful(List(obj)))
      val result = controller.index()
                  .apply(FakeRequest("GET", "/dashboard")
                  .withSession("arn" -> "arn"))
      status(result) shouldBe 200
    }

    "return 400 BadRequest" in {
      when(clientService.processAllClientsData(any())) thenReturn(Future.successful(List(obj)))
      val result = controller.index()
                  .apply(FakeRequest("GET", "/dashboard"))
      status(result) shouldBe 400
    }
  }



}
