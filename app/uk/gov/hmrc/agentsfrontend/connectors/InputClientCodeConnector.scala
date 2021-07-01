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

import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentClient

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class InputClientCodeConnector @Inject()(ws: WSClient, ec: ExecutionContext) {

  def postClientCode(agentClientCode: AgentClient) = {

    ws.url("http://localhost:9006/addAgent").post(Json.obj("crn" -> agentClientCode.crn,
      "arn" -> agentClientCode.arn)) map {
      _.status match {
        case 204 => 204 // redirect to success page
        case 404 => 404 // bad input not a client code
        case 409 => 409 // client already has an agent
        case _ => false
      }
    }
  }
}



