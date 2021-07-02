
package uk.gov.hmrc.agentsfrontend.connectors

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.agentsfrontend.models.Client
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class DashBoardConnector @Inject()(ws: WSClient) {

  def getAllClientsData(arn: String):Future[List[Client]]= {

    ws.url(s"http://localhost:9006/readAllAgent").post(Json.obj("arn" -> arn)).map { x =>
      x.status match {
        case 200 => x.json.as[JsArray].value.flatMap(response => Some(Client(
          (response \ "crn").as[String],
          (response \ "name").as[String],
          (response \ "businessName").as[String],
          (response \ "contactNumber").as[String],
          (response \ "propertyNumber").as[Int],
          (response \ "postcode").as[String],
          (response \ "businessType").as[String],
          (response \ "arn").as[String]
        ))).toList
        case _ => List()
      }
    }
  }
}



