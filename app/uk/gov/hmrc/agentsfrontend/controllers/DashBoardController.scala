
package uk.gov.hmrc.agentsfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentsfrontend.connectors.DashBoardConnector
import uk.gov.hmrc.agentsfrontend.views.html.Index
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DashBoardController @Inject()( mcc: MessagesControllerComponents,
                                     indexPage: Index,
                                     connector: DashBoardConnector)
                                     extends FrontendController(mcc) {


  def index: Action[AnyContent] = Action.async { implicit request =>
    request.session.get("arn")  match {
      case Some(arn)    => connector.getAllClientsData(arn).map(x => Ok(indexPage(arn, x)))
      case _            => Future(Redirect(routes.AgentLoginController.agentLogin()))
    }
  }
}
