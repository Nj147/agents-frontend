package uk.gov.hmrc.agentsfrontend.connectors

import play.api.libs.ws.WSClient
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject

class AgentDetailsConnector @Inject()(ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController{

}
