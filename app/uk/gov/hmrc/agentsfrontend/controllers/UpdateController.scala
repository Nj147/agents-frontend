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
import uk.gov.hmrc.agentsfrontend.controllers.predicates.LoginChecker
import uk.gov.hmrc.agentsfrontend.views.html.UpdatePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class UpdateController @Inject()(
                                  mcc: MessagesControllerComponents,
                                  connector: AgentDetailsConnector,
                                  loginChecker: LoginChecker,
                                  updatePage: UpdatePage
                                ) extends FrontendController(mcc) {

  def getDetails: Action[AnyContent] = Action.async { implicit request =>

    loginChecker.isLoggedIn(arn => connector.getAgentDetails(arn).map {
      case Some(agent) => Ok(updatePage(agent))
      case None => InternalServerError
    })
  }
}
