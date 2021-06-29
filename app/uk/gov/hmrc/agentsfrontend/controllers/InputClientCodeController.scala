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

package uk.gov.hmrc.agentsfrontend.controllers

import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentsfrontend.views.html.{Home, InputClientCode}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class InputClientCodeController @Inject()(     ws: WSClient,
                                               mcc: MessagesControllerComponents,
                                               clientCode: InputClientCode)
  extends FrontendController(mcc) {

  val inputClientCode = Action { implicit request =>
    Ok(clientCode())
  }

 // val agentCode = "testAgent"
//  lazy val arn = request.session.get("arn").get
}




/*
* get input from form
*
* send input + agent code to them, if client exists and is not already connected to agent they can update their DB if not do nothing
* match on client status code returned
*
* */

//  def ClientCodePost(): Action[AnyContent] = Action.async { implicit request =>
//    val postData = request.body.asFormUrlEncoded
//
//    val vehicleName = postData.map { args =>
//      args("Vehicle Name").head
//    }.getOrElse(Ok("Error"))

//    val dataToBeSent = Json.obj(
//      "crn" -> s"${input}",
//      "arn" -> s"${agentCode}"
//    )
//    val futureResponse: Future[WSResponse] = ws.url("http://localhost:9006/addAgent").post(dataToBeSent)

//    futureResponse.map {
//      response =>
//        val js = Json.fromJson[Vehicle](response.json)
//        val veh = js.get
//        Ok(views.html.vehicle(veh))
//    } recover {
//      case _ => NotFound
//    }
//  }
//
//
//
//
//    def submitClientCode() = Action { implicit request =>
//      InputClientCode.form.bindFromRequest().fold(
//        formWithErrors => BadRequest(views.html.inputClientCode(formWithErrors)),
//        success =>
//      )
//    }
//}
//case class InputClientCode(code: String)
//object InputClientCode {
//
//  val form: Form[InputClientCode] = Form(
//    mapping(
//      "crn" -> text,
//    )(InputClientCode.apply)(InputClientCode.unapply)
//  )
//}
