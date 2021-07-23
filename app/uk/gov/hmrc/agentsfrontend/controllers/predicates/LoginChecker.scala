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

package uk.gov.hmrc.agentsfrontend.controllers.predicates

import play.api.mvc.{AnyContent, MessagesControllerComponents, MessagesRequest, Result}
import uk.gov.hmrc.agentsfrontend.controllers.routes
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future

class LoginChecker @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc){

  def isLoggedIn(func:(String) => Future[Result])(implicit request: MessagesRequest[AnyContent]): Future[Result] = {
    Try{
      request.session.get("arn").get
    } match {
      case Success(arn) => func(arn)
      case Failure(_) => Future(Redirect(routes.AgentLoginController.agentLogin()))
    }
  }

}
