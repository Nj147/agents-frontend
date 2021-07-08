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
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._
import play.api.mvc.{BaseController, ControllerComponents}
import uk.gov.hmrc.agentsfrontend.models.AgentLogin
import scala.concurrent.duration.DurationInt

class AgentConnector @Inject()(ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController {

  def wspost(url: String, jsObject: JsObject): Future[WSResponse] = {
    ws.url(url).withRequestTimeout(5000.millis).post(jsObject)
  }

  def checkLogin(agentLogin: AgentLogin): Future[Int] = {
    val newLogin = Json.obj(
      "arn" -> agentLogin.arn,
      "password" -> agentLogin.password
    )
    wspost("http://localhost:9009/check-agent-login", newLogin).map {
      _.status
    }
  }
}