package uk.gov.hmrc.agentsfrontend.controllers

import org.jsoup.Jsoup
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, session, status}

class UpdateContactNumberControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite{

  val controller: UpdateContactNumberController = app.injector.instanceOf[UpdateContactNumberController]
  private val fakeRequest = FakeRequest("/GET", "/update-contact")
  private val fakePostRequest = FakeRequest("/POST", "/update-contact")

  "GET /update-contact" should {
    "return status 200 with a contact number form field" when {
      "the user is logged in as an agent" in {
        val result = controller.startPage.apply(fakeRequest.withSession("arn" -> "ARN0000001"))
        status(result) shouldBe OK
        Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-input--width-10").size shouldBe 1
      }
    }
    "redirect to the home page" when {
      "an agent is not logged in" in {
        val result = controller.startPage.apply(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
    "have a prefilled email value in the form field" when {
      "the logged in user opens the page" in {
        val result = controller.startPage.apply(fakeRequest.withSession("arn" -> "ARN0000001", "contactNumber" -> "01234567898"))
        Jsoup.parse(contentAsString(result)).getElementById("number").`val`() shouldNot be("")
      }
    }
  }

  "POST /update-contact-number" should {
    "return a bad request and a form with errors" when {
      "the user submits a form with an invalid contact number" in {
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "123456"))
        status(result) shouldBe BAD_REQUEST
        Jsoup.parse(contentAsString(result)).text() should include("Please enter a valid phone number")
      }
    }
    "redirect back to the dashboard [update summary] with updated session values" when {
      "a valid contact number is submitted" in {
        val result = controller.processContactNumber.apply(fakePostRequest.withFormUrlEncodedBody("number" -> "01234567890"))
        status(result) shouldBe SEE_OTHER
        session(result).get("contactNumber") shouldBe Some("01234567890")
        redirectLocation(result) shouldBe Some(s"${routes.DashBoardController.index()}")
      }
    }
  }

}
