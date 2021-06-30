package uk.gov.hmrc.agentsfrontend.controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsfrontend.views.html.StartPage

class StartPageControllerSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val startPage = app.injector.instanceOf[StartPage]
  private val controller = new StartController(Helpers.stubMessagesControllerComponents(), startPage)

  "start" should {
    "return OK status" when {
      "redirected to log in page" in {
        val result = controller.start.apply(FakeRequest())
        status(result) shouldBe Status.OK
      }
    }
  }
}
