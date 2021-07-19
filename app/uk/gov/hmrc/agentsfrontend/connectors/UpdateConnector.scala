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
import play.api.mvc.{BaseController, ControllerComponents}
import uk.gov.hmrc.agentsfrontend.models.Address

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateConnector @Inject()(ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController {

  def updateContactNumber(arn: String, contactNumber: Long): Future[Boolean] = ws.url(s"http://localhost:9009/agents/${arn}/contact-number")
    .patch(Json.obj("contactNumber" -> contactNumber))
    .map {
      _.status match {
        case 200 => true
        case _ => false
      }
    }

  def updateCorrespondence(arn: String, moc: List[String]): Future[Boolean] = ws.url(s"http://localhost:9009/agents/${arn}/correspondence")
    .patch(Json.obj("modes" -> moc))
    .map {
      _.status match {
        case 200 => true
        case _ => false
      }
    }

  def updateAddress(arn: String, address: Address) = {
    ws.url(s"http://localhost:9009/agents/${arn}/address").patch(Json.toJson(address)).map { response =>
      response.status match {
        case 200 => true
        case _ => false
      }
    }
  }

  def updateEmail(arn: String, email: String): Future[Boolean] = ws.url(s"http://localhost:9009/agents/${arn}/update-email")
    .patch(Json.obj("email" -> email))
    .map {
      _.status match {
        case 200 => true
        case _ => false
      }
    }
}
