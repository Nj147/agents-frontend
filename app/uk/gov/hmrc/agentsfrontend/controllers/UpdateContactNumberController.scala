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
import uk.gov.hmrc.agentsfrontend.models.ContactNumber
import uk.gov.hmrc.agentsfrontend.views.html.ContactNumberPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class UpdateContactNumberController @Inject()(mcc: MessagesControllerComponents, updatePage: ContactNumberPage, connector: UpdateConnector) extends FrontendController(mcc) {

  def startPage: Action[AnyContent] = Action { implicit request =>
    request.session.get("arn") match {
      case Some(arn) => Ok(updatePage(ContactNumber.contactForm))
      case None => Redirect(routes.AgentLoginController.agentLogin())
    }
  }

  def processContactNumber: Action[AnyContent] = Action async { implicit request =>
    Try {
      request.session.get("arn").get
    } match {
      case Success(value) => ContactNumber.contactForm.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(updatePage(formWithErrors))),
        cNum => connector.updateContactNumber(value, cNum.number.toLong).map {
          case true => Redirect(routes.UpdateController.getDetails())
          case false => BadRequest(updatePage(ContactNumber.contactForm.withError("number", "Change cannot be made, please try again")))
        })
      case Failure(_) => Future.successful(Redirect(routes.AgentLoginController.agentLogin()))
    }
  }
}
