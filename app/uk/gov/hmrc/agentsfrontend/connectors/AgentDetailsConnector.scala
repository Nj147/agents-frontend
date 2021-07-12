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

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{BaseController, ControllerComponents}
import uk.gov.hmrc.agentsfrontend.models.AgentDetails

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class AgentDetailsConnector @Inject()(ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController {

  def wsput(url: String, jsObject: JsValue): Future[WSResponse]= {
    ws.url(url).withRequestTimeout(5000.millis).put(jsObject)
  }

  def updateAgent(agentDetails: AgentDetails): Future[Boolean] = {
    val toUpdate = Json.toJson(AgentDetails(
      agentDetails.arn,
      agentDetails.businessName,
      agentDetails.email,
      agentDetails.mobileNumber,
      agentDetails.moc,
      agentDetails.propertyNumber,
      agentDetails.postcode
    ))
    wsput("http://localhost:9009/update-agent", toUpdate).map { response =>
      response.status match {
        case 202 => true
        case 500 => false
      }
    }
  }


}
