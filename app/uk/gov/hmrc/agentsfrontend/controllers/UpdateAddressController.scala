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
import uk.gov.hmrc.agentsfrontend.models.Address
import uk.gov.hmrc.agentsfrontend.views.html.UpdateAddressPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class UpdateAddressController @Inject()(mcc: MessagesControllerComponents, addressPage: UpdateAddressPage, conn: UpdateConnector) extends FrontendController(mcc) {

  def startPage: Action[AnyContent] = Action { implicit request =>
    request.session.get("arn") match {
      case Some(arn) => Ok(addressPage(Address.addressForm))
      case None => Redirect(routes.AgentLoginController.agentLogin())
    }
  }

  def processAddress: Action[AnyContent] = Action.async { implicit request =>
    Try {
      request.session.get("arn").get
    } match {
      case Success(value) =>
        Address.addressForm.bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(addressPage(formWithErrors))),
          address => conn.updateAddress(value, Address(address.propertyNumber, address.postcode)).map {
            case true => Redirect(routes.UpdateController.getDetails())
            case false => BadRequest(addressPage(Address.addressForm.withError("propertyNumber", "Change of details could not be process, please try again")))
          }
        )
      case Failure(_) => Future.successful(Redirect(routes.AgentLoginController.agentLogin()))
    }
  }
}
