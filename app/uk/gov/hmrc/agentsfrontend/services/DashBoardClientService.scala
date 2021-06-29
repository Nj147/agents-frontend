package uk.gov.hmrc.agentsfrontend.services

import akka.stream.impl.Stages.DefaultAttributes.recover
import play.api.libs.json.Json
import play.api.libs.ws
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentsfrontend.views.html.Index
import uk.gov.hmrc.govukfrontend.views.viewmodels.cookiebanner.Action
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class DashBoardClientService @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc){

//  def getClientData() = Action.async { implicit request =>
//
//    val arnToSend = Json.obj(
//      "arn" -> "someArn"
//    )
//    val futureResult = ws.url(s"http://localhost:9006/readAllAgent").post(arnToSend)
//
//    futureResult.map { response =>
//      val js = Json.fromJson[Vehicle](response.json)
//      val veh = js.get
//      Ok(views.html.vehicle(veh))
//    } recover {
//        case _ => NotFound
//    }
//  }
}
