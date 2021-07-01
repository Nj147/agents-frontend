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
import uk.gov.hmrc.agentsfrontend.persistence.domain.{AgentClient, Client}
import uk.gov.hmrc.agentsfrontend.views.html.InputClientCode
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InputClientCodeController @Inject()(mcc: MessagesControllerComponents, clientCode: InputClientCode, post: uk.gov.hmrc.agentsfrontend.services.InputClientCodeService)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with play.api.i18n.I18nSupport {

  def getInputClientCode: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(clientCode(Client.form))
  }

  def submitClientCode: Action[AnyContent] = Action async { implicit request =>
    Client.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(clientCode(formWithErrors))),
      response => post.postClientCode(AgentClient(request.session.get("arn").get, response.crn)) map {
        case 204 => Redirect(routes.SuccessClientCodeController.successClientCode())
        case 404 => NotFound(clientCode(Client.form.withError("crn", "wrong client code entered")))
        case 409 => Conflict(clientCode(Client.form.withError("crn", "this client already has an agent")))
      })
  }
}






