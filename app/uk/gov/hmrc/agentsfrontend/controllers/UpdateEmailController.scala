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
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.controllers.predicates.LoginChecker
import uk.gov.hmrc.agentsfrontend.models.Email
import uk.gov.hmrc.agentsfrontend.views.html.UpdateEmailPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UpdateEmailController @Inject()(mcc: MessagesControllerComponents,
                                      updateEmailPage: UpdateEmailPage,
                                      loginChecker: LoginChecker,
                                      connector: UpdateConnector) extends FrontendController(mcc) {

  def displayUpdateEmailPage(): Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(_ => Future.successful(Ok(updateEmailPage(Email.emailForm))))
  }

  def processUpdateEmail(): Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(arn => Email.emailForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(updateEmailPage(formWithErrors))),
      email => connector.updateEmail(arn, email.email) map {
        case true => Redirect(routes.UpdateController.getDetails())
        case false => BadRequest(updateEmailPage(Email.emailForm.withError("email", "Change cannot be made, please try again")))
      }))
  }
}