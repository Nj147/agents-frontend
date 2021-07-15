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
import uk.gov.hmrc.agentsfrontend.config.ErrorHandler
import uk.gov.hmrc.agentsfrontend.connectors.AgentLoginConnector
import uk.gov.hmrc.agentsfrontend.models.AgentLogin
import uk.gov.hmrc.agentsfrontend.views.html.AgentLoginPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentLoginController @Inject()(
                                      mcc: MessagesControllerComponents,
                                      agentLoginPage: AgentLoginPage,
                                      ac: AgentLoginConnector,
                                      error: ErrorHandler
                                    ) extends FrontendController(mcc) {

  def agentLogin: Action[AnyContent] = Action { implicit request =>
    request.session.get("arn") match {
      case Some(_) => Redirect(routes.DashBoardController.index())
      case _ => Ok(agentLoginPage(AgentLogin.submitForm))
    }
  }

  def agentLoginSubmit: Action[AnyContent] = Action.async { implicit request =>
    AgentLogin.submitForm.bindFromRequest().fold({ formWithErrors =>
      Future.successful(BadRequest(agentLoginPage(formWithErrors)))
    }, { agentLogin =>
      ac.checkLogin(agentLogin).map {
        case 200 => Redirect(routes.DashBoardController.index()).withSession(request.session + ("arn" -> agentLogin.arn))
        case 401 => Unauthorized(agentLoginPage(AgentLogin.submitForm.withError("arn", "Login does not exist")))
        case 500 => InternalServerError(error.standardErrorTemplate("", "", ""))
      }
    })
  }

  def logout: Action[AnyContent] = Action {
    Redirect(routes.AgentLoginController.agentLogin()).withNewSession
  }

}