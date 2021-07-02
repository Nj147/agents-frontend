package uk.gov.hmrc.agentsfrontend.controllers

import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.agentsfrontend.connectors.InputClientCodeConnector
import uk.gov.hmrc.agentsfrontend.persistence.domain.AgentClient
import uk.gov.hmrc.agentsfrontend.services.InputClientCodeService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InputClientCodeServiceISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {
  val connector: InputClientCodeConnector = mock(classOf[InputClientCodeConnector])
  val service = new InputClientCodeService(connector)

  "postClientCode" should {
    "return 204" when {
      "Agent code is successfully added" in {
        when(connector.postClientCode(any())) thenReturn (Future(204))
        val result = service.postClientCode(AgentClient("Agent", "Client"))
        await(result) shouldBe (204)
      }
    }
    "return 404" when {
      "Client code provided does not exist in Client database" in {
        when(connector.postClientCode(any())) thenReturn (Future(404))
        val result = service.postClientCode(AgentClient("Agent", "Error in Client Code"))
        await(result) shouldBe (404)
      }
    }
    "return 409" when {
      "Client already has an associated Agent" in {
        when(connector.postClientCode(any())) thenReturn (Future(409))
        val result = service.postClientCode(AgentClient("Agent", "Client code already in use"))
        await(result) shouldBe (409)
      }
    }
  }
}