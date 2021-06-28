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

import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.agentsfrontend.connectors.AgentConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.{AgentLogin, AgentLoginForm}
import uk.gov.hmrc.agentsfrontend.views.html.AgentLoginPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global


import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentLoginController @Inject()(
                                    mcc: MessagesControllerComponents,
                                    agentLoginPage: AgentLoginPage,
                                    ac: AgentConnector
                                    ) extends FrontendController(mcc) {

  val agentLogin = Action { implicit request =>
    val form: Form[AgentLogin] = request.session.get("agentLogin").fold(AgentLoginForm.submitForm.fill(AgentLogin("",""))) { data =>
      AgentLoginForm.submitForm.fill(AgentLogin.decode(data))
    }
    Ok(agentLoginPage(form))
  }

  val agentLoginSubmit = Action.async { implicit request =>
    AgentLoginForm.submitForm.bindFromRequest().fold({ formWithErrors =>
      Future.successful(BadRequest(agentLoginPage(formWithErrors)))
    }, { agentLogin =>
      ac.checkLogin(agentLogin).map {
        case true => Redirect(routes.HomeController.home()).withSession("arn" -> agentLogin.arn + "isLoggedIn" -> "true")
        case false => BadRequest("Login unsuccessful")
      }
    })
  }
}