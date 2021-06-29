package uk.gov.hmrc.agentsfrontend.models

import play.api.libs.json.Json

case class Client (crn: String,
                   name: String,
                   businessName: String,
                   contactNumber: String,
                   propertyNumber: Int,
                   postCode: String,
                   businessType: String,
                   arn: Option[String])

object Client {
  implicit val format = Json.format[Client]
}
