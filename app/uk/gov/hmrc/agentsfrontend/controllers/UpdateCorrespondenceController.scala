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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.agentsfrontend.models.Correspondence.correspondenceForm
import uk.gov.hmrc.agentsfrontend.views.html.UpdateCorrespondencePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UpdateCorrespondenceController @Inject()(mcc: MessagesControllerComponents, updateMOC: UpdateCorrespondencePage)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with play.api.i18n.I18nSupport {

  def getCorrespondence: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(updateMOC(form = correspondenceForm))
  }

  def updateCorrespondence(): Action[AnyContent] = Action { implicit request =>
    val response = correspondenceForm.bindFromRequest.get
    response.modes.size match {
      case 0 => BadRequest(updateMOC(correspondenceForm.withError("modes", "Please select at least one method of correspondence")))
      case _ => Redirect(routes.DashBoardController.index()) // should re-direct to summary page with modes + arn
    }
  }
}




