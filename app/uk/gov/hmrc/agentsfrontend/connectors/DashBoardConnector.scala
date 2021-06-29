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

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.agentsfrontend.models.Client
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DashBoardConnector @Inject()(ws: WSClient) {

    def getAllClientsData:Future[List[Client]]={
      val arnToSend = Json.obj(
        "arn" -> "someArn"
      )

      ws.url(s"http://localhost:9006/readAllAgent").post(arnToSend)
        .map { x =>
          x.status match {
            case 200 => x.json.as[JsArray].value.flatMap(response => Some(Client(
              (response \ "crn").as[String],
              (response \ "name").as[String],
              (response \ "businessName").as[String],
              (response \ "contactNumber").as[String],
              (response \ "propertyNumber").as[Int],
              (response \ "postCode").as[String],
              (response \ "businessType").as[String],
              (response \ "arn").as[String]
            ))).toList
            case _ => List()
          }
        }
      }


//      Future (List( Client("AABCCD",
//                    "Elon Musk",
//                    "SpaceX",
//                    "08977643456",
//                     8,
//                    "BS166FGJ",
//                    "Space Exploration",
//                    "ABBCVDDE"),
//                    Client("AADSCCD",
//                     "Elon Musk",
//                     "SpaceX",
//                     "08977643456",
//                     7,
//                     "BS166FGJ",
//                     "Space Exploration",
//                     "AVVCVDDE")
//                    ))


}




