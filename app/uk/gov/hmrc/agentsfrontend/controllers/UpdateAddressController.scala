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
import uk.gov.hmrc.agentsfrontend.models.Address
import uk.gov.hmrc.agentsfrontend.views.html.AddressPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class UpdateAddressController @Inject()( mcc: MessagesControllerComponents, addressPage: AddressPage) extends FrontendController(mcc){

  def startPage: Action[AnyContent] = Action { implicit request =>
    request.session.get("arn") match {
      case Some(arn) => request.session.get("address").fold(
        Ok(addressPage(Address.addressForm.fill(Address(propertyNumber= "", postcode = ""))))
      )
      {address => Ok(addressPage(Address.addressForm.fill(Address.decode(address))))}

      case None => Redirect(routes.StartController.start())
    }
  }

  def processAddress: Action[AnyContent] = Action { implicit request =>
    Address.addressForm.bindFromRequest().fold(
      formWithErrors => BadRequest(addressPage(formWithErrors)),
      address => Redirect(routes.DashBoardController.index()).withSession(request.session + ("address" -> address.encode))
    )
  }
}
