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

import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentsfrontend.controllers.predicates.LoginChecker
import uk.gov.hmrc.agentsfrontend.models.ClientCode
import uk.gov.hmrc.agentsfrontend.views.html.{InputClientCode, SuccessClientCode}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import uk.gov.hmrc.agentsfrontend.services.InputClientCodeService
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class InputClientCodeController @Inject()(mcc: MessagesControllerComponents,
                                          clientCode: InputClientCode,
                                          loginChecker: LoginChecker,
                                          success: SuccessClientCode,
                                          post: InputClientCodeService) extends FrontendController(mcc) {

  def getInputClientCode: Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(_ => Future.successful(Ok(clientCode(ClientCode.form))))
  }

  def submitClientCode: Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(arn => ClientCode.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(clientCode(formWithErrors))),
      response => post.postClientCode(arn, response.crn) map {
        case 204 => Ok(success())
        case 404 => NotFound(clientCode(ClientCode.form.withError("crn", "Wrong client code entered")))
        case 409 => Conflict(clientCode(ClientCode.form.withError("crn", "This client already has an agent")))
        case _ => InternalServerError
      })
    )
  }
}






