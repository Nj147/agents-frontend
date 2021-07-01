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
import uk.gov.hmrc.agentsfrontend.connectors.DashBoardConnector
import uk.gov.hmrc.agentsfrontend.views.html.Index
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DashBoardController @Inject()( mcc: MessagesControllerComponents,
                                     indexPage: Index,
                                     connector: DashBoardConnector)
                                     extends FrontendController(mcc) {


  def index: Action[AnyContent] = Action.async { implicit request =>

    request.session.get("arn") match {
      case Some(arn)   => connector.getAllClientsData(arn)
                          .map(x => Ok(indexPage(arn, x)))

      case _           => Future (BadRequest)
    }
  }
}
