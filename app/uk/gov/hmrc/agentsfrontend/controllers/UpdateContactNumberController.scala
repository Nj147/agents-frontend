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
import uk.gov.hmrc.agentsfrontend.models.{ContactNumber, UpdateContactNumber}
import uk.gov.hmrc.agentsfrontend.views.html.ContactNumberPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future

class UpdateContactNumberController @Inject()(mcc: MessagesControllerComponents, updatePage: ContactNumberPage, connector: UpdateConnector) extends FrontendController(mcc) {

  def startPage: Action[AnyContent] = Action { implicit request =>
    Ok(updatePage(ContactNumber.contactForm))
  }

  def processContactNumber: Action[AnyContent] = Action async { implicit request =>
    ContactNumber.contactForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(updatePage(formWithErrors))),
      cNum => connector.updateContactNumber(UpdateContactNumber(request.session.get("arn").get, cNum.number.toLong)).map {
        case true => Redirect(routes.DashBoardController.index())
        case false => NotAcceptable(updatePage(ContactNumber.contactForm.withError("number", "Change cannot be made, please try again")))
      }
    )
  }
}
