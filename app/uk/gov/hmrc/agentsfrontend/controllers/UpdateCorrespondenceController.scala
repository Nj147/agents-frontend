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
import uk.gov.hmrc.agentsfrontend.models.Correspondence
import uk.gov.hmrc.agentsfrontend.views.html.UpdateCorrespondencePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentsfrontend.connectors.UpdateConnector
import uk.gov.hmrc.agentsfrontend.controllers.predicates.LoginChecker
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UpdateCorrespondenceController @Inject()(mcc: MessagesControllerComponents,
                                               connector: UpdateConnector,
                                               loginChecker: LoginChecker,
                                               updateMOC: UpdateCorrespondencePage) extends FrontendController(mcc) {

  def getCorrespondence: Action[AnyContent] = Action async { implicit request =>
    loginChecker.isLoggedIn(_ => Future.successful(Ok(updateMOC(Correspondence.form))))
  }

  def updateCorrespondence(): Action[AnyContent] = Action async { implicit request =>
    val response = Correspondence.form.bindFromRequest.get
    loginChecker.isLoggedIn(arn => response.modes.size match {
      case 0 => Future.successful(BadRequest(updateMOC(Correspondence.form.withError("modes", "Please select at least one method of correspondence"))))
      case _ => connector.updateCorrespondence(arn, response.modes).map {
        case true => Redirect(routes.UpdateController.getDetails())
        case false => BadRequest(updateMOC(Correspondence.form.withError("modes", "Change cannot be made, please try again")))
      }
    })
  }
}




