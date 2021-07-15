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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.agentsfrontend.models.ClientCode
import uk.gov.hmrc.agentsfrontend.views.html.InputClientCode
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.agentsfrontend.services.InputClientCodeService
import scala.util.{Failure, Success, Try}

@Singleton
class InputClientCodeController @Inject()(mcc: MessagesControllerComponents, clientCode: InputClientCode, post: InputClientCodeService)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with play.api.i18n.I18nSupport {

  def getInputClientCode: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.session.get("arn") match {
      case Some(_) => Ok(clientCode(ClientCode.form))
      case _ => Redirect(routes.AgentLoginController.agentLogin())
    }
  }

  def submitClientCode: Action[AnyContent] = Action async { implicit request =>
    Try {
      request.session.get("arn").get
    } match {
      case Success(value) => ClientCode.form.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(clientCode(formWithErrors))),
        response => post.postClientCode(value, response.crn) map {
          case 204 => Redirect(routes.SuccessClientCodeController.successClientCode())
          case 404 => NotFound(clientCode(ClientCode.form.withError("crn", "Wrong client code entered")))
          case 409 => Conflict(clientCode(ClientCode.form.withError("crn", "This client already has an agent")))
          case _ => InternalServerError
        })
      case Failure(_) => Future.successful(Redirect(routes.AgentLoginController.agentLogin()))
    }
  }
}






