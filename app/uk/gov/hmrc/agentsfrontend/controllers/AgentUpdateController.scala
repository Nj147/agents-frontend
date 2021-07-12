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
import uk.gov.hmrc.agentsfrontend.connectors.AgentUpdateConnector
import uk.gov.hmrc.agentsfrontend.models.{Address, AgentDetails, Correspondence}
import uk.gov.hmrc.agentsfrontend.views.html.UpdatePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AgentUpdateController @Inject()(
                                       mcc: MessagesControllerComponents,
                                       updatePage: UpdatePage,
                                       ac: AgentUpdateConnector,
                                    ) extends FrontendController(mcc) {

  val agentUpdate: Action[AnyContent] = Action { implicit request =>
    request.session.get("arn") match {
      case Some(_) =>
        val agentToUpdate: AgentDetails = AgentDetails(
          request.session.get("arn").get,
          request.session.get("businessName").get,
          request.session.get("email").get,
          request.session.get("mobileNumber").get.toLong,
          Correspondence.decode(request.session.get("moc").get),
          Address.decode(request.session.get("Address").get).propertyNumber,
          Address.decode(request.session.get("Address").get).postcode
        )
        Ok(updatePage(agentToUpdate))
      case _ => Redirect(routes.AgentLoginController.agentLogin())
    }
  }

  val agentUpdateSubmit: Action[AnyContent] = Action.async { implicit request =>
    val agentToUpdate: AgentDetails = AgentDetails(
      request.session.get("arn").get,
      request.session.get("businessName").get,
      request.session.get("email").get,
      request.session.get("mobileNumber").get.toLong,
      Correspondence.decode(request.session.get("moc").get),
      Address.decode(request.session.get("Address").get).propertyNumber,
      Address.decode(request.session.get("Address").get).postcode
    )
    ac.updateAgent(agentToUpdate).map {
      case true => Ok("Update Success!")
      case false => BadRequest("Update failed!")
    }
  }
}
