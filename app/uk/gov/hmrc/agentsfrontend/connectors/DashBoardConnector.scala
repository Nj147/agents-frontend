package uk.gov.hmrc.agentsfrontend.connectors

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient

import javax.inject.Inject

class DashBoardConnector @Inject()(ws: WSClient){

    def getAllClientsData()={
      val arnToSend = Json.obj(
        "arn" -> "someArn"
      )
      ws.url(s"http://localhost:9006/readAllAgent").post(arnToSend)
        .map(_.json.as[JsArray].value.flatMap( response => Some(Client()

    }
}

//.map(_.json.as[JsArray].value.flatMap( response => Some(Person(
//(response \ "firstName").as[String],
//(response \ "lastName").as[String],
//(response \ "jobTitle").as[String],
//(response \ "jobIndustry").as[String],
//(response \ "email").as[String],
//(response \ "firstline").as[String],
//(response \ "secondline").as[String],
//(response \ "city").as[String],
//(response \ "postcode").as[String]
//))).toSeq)