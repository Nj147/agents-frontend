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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentsfrontend.connectors.AgentDetailsConnector
import uk.gov.hmrc.agentsfrontend.views.html.StartPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

class AgentDetailsController @Inject()(
                                        mcc: MessagesControllerComponents,
                                        connector: AgentDetailsConnector,
                                        updatePage: StartPage
                                      ) extends FrontendController(mcc) {

  def getUpdatePage: Action[AnyContent] = Action async { implicit request =>
    connector.getAgentDetails(request.session.get("arn").get).map {
      response =>
        Ok(updatePage()).withSession(request.session + ("businessName" -> response.businessName) + ("email" -> response.email) + ("mobileNumber" -> response.mobileNumber.toString) + ("moc" -> response.moc.mkString(",")) + ("address" -> (response.propertyNumber + "/" + response.postcode)))
    }
  }

}
