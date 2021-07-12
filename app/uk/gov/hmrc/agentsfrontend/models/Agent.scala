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

package uk.gov.hmrc.agentsfrontend.models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.{Json, OFormat}

case class Agent(name: String)

case class AgentClient(arn: String, crn: String)

object AgentClient {
  implicit val format: OFormat[AgentClient] = Json.format[AgentClient]
}

case class ClientCode(crn: String)

object ClientCode {
  val form: Form[ClientCode] = Form(
    mapping(
      "crn" -> nonEmptyText
    )(ClientCode.apply)(ClientCode.unapply)
  )
}

case class AgentDetails(arn: String, businessName: String, email: String, mobileNumber: Long, moc: Seq[String], propertyNumber: String, postcode: String)

object AgentDetails {
  implicit val format: OFormat[AgentDetails] = Json.format[AgentDetails]
}

