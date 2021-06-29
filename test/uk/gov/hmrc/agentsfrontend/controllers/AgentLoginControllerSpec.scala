package uk.gov.hmrc.agentsfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.connectors.AgentConnector
import uk.gov.hmrc.agentsfrontend.views.html.{AgentLoginErrorPage, AgentLoginPage}

import scala.concurrent.Future

class AgentLoginControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val ac = mock(classOf[AgentConnector])
  private val agentLoginPage = app.injector.instanceOf[AgentLoginPage]
  private val agentLoginErrorPage = app.injector.instanceOf[AgentLoginErrorPage]
  private val controller = new AgentLoginController(Helpers.stubMessagesControllerComponents(), agentLoginPage, agentLoginErrorPage, ac)

  "agentLogin" should {
    "return status 200" in {
      val result = controller.agentLogin.apply(FakeRequest())
      status(result) shouldBe 200
    }

  }
  "agentLoginSubmit" should {
    "return bad request if some fields are blank" in {
      val result = controller.agentLoginSubmit.apply(FakeRequest().withFormUrlEncodedBody("arn" -> "", "password" -> "pa55w0rd"))
      status(result) shouldBe 400
    }
    "redirect if all fields filled in" in {
      when(ac.checkLogin(any())) thenReturn Future.successful(true)
      val result = controller.agentLoginSubmit.apply(FakeRequest().withFormUrlEncodedBody("arn" -> "3242MKOSD", "password" -> "pa55w0rd"))
      status(result) shouldBe 303
    }
  }
}
