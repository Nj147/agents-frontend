package uk.gov.hmrc.agentsfrontend.connectors

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.agentsfrontend.models.Client
import javax.inject.Inject
import scala.concurrent.Future

class DashBoardConnector @Inject()(ws: WSClient) {

    def getAllClientsData():Future[Seq[Client]]={
      val arnToSend = Json.obj(
        "arn" -> "someArn"
      )

      ws.url(s"http://localhost:900_/readAllAgent").post(arnToSend)
        .map(_.json.as[JsArray].value.flatMap( response => Some(Client(
          (response \ "crn").as[String],
          (response \ "name").as[String],
          (response \ "businessName").as[String],
          (response \ "contactNumber").as[String],
          (response \ "propertyNumber").as[Int],
          (response \ "postCode").as[String],
          (response \ "businessType").as[String],
          (response \ "arn").as[Option[String]]
        ))).toSeq)
    }
}





