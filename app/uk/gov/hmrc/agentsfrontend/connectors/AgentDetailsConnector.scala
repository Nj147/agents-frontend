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

package uk.gov.hmrc.agentsfrontend.connectors

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{BaseController, ControllerComponents}
import uk.gov.hmrc.agentsfrontend.models.AgentDetails
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future

class AgentDetailsConnector @Inject()(ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController {

  def getAgentDetails(arn: String): Future[Option[AgentDetails]] = {
    ws url (s"http://localhost:9009/agents/$arn/details") post (Json.toJson(arn)) map {
      response => response.json.validate[AgentDetails] match {
        case JsSuccess(agent, _) => Some(agent)
        case JsError(_) => None
      }
    }
  }

}
