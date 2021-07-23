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
import uk.gov.hmrc.agentsfrontend.models.ContactNumber
import uk.gov.hmrc.agentsfrontend.views.html.UpdateContactNumberPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UpdateContactNumberController @Inject()(mcc: MessagesControllerComponents,
                                              updatePage: UpdateContactNumberPage,
                                              loginChecker: LoginChecker,
                                              connector: UpdateConnector) extends FrontendController(mcc) {

  def startPage: Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(_ => Future.successful(Ok(updatePage(ContactNumber.contactForm))))
  }

  def processContactNumber: Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(arn => ContactNumber.contactForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(updatePage(formWithErrors))),
      cNum => connector.updateContactNumber(arn, cNum.number).map {
        case true => Redirect(routes.UpdateController.getDetails())
        case false => BadRequest(updatePage(ContactNumber.contactForm.withError("number", "Change cannot be made, please try again")))
      }
    ))
  }
}
