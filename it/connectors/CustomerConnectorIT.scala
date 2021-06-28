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

package connectors

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.baseApplicationBuilder.injector
import traits.WireMockHelper
import uk.gov.hmrc.examplefrontend.connectors.CustomerConnector
import uk.gov.hmrc.examplefrontend.persistence.domain.Customer

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration.Duration


class CustomerConnectorIT extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WireMockHelper with BeforeAndAfterEach{
  lazy val connector: CustomerConnector = injector.instanceOf[CustomerConnector]
  private val cust1 = Customer("First", "Last", 123456789, "test1@test.com", false, "01/01/2000")
  private val cust2 = Customer("FirstName", "LastName", 987654321, "test2@test.com", false, "31/12/1999")

  def await[T](awaitable: Awaitable[T]): T = Await.result(awaitable, Duration.Inf)

  override def beforeEach() = startWireMock()

  override def afterEach() = stopWireMock()

  "POST /create" should {
    "return true when created response returned" in {
      stubPost("/create",201, "")
      val result = await(connector.create(Customer("John", "Smith", 123456789, "test@example.com", true, "01/01/2000")))
      result shouldBe true
    }
    "return false when bad request response returned" in {
      stubPost("/create",400, "")
      val result = await(connector.create(Customer("John", "Smith", 123456789, "test@example.com", true, "01/01/2000")))
      result shouldBe false
    }
  }

  "GET /readAll" should {
    "return a future sequence of movies" in {
      stubGet("/readAll", 200, s"[${Json.obj(
        "firstName" -> cust1.firstName,
        "lastName" -> cust1.lastName,
        "phoneNumber" -> cust1.phoneNumber,
        "email" -> cust1.email,
        "britishCitizen" -> cust1.britishCitizen,
        "dateOfBirth" -> cust1.dateOfBirth)}," +
        s"${Json.obj(
          "firstName" -> cust2.firstName,
          "lastName" -> cust2.lastName,
          "phoneNumber" -> cust2.phoneNumber,
          "email" -> cust2.email,
          "britishCitizen" -> cust2.britishCitizen,
          "dateOfBirth" -> cust2.dateOfBirth)}]")
      val result = await(connector.readAll())
      result shouldBe Seq(cust1, cust2)
    }
  }

  "GET /readOne" should {
    "return a single customer when looking for a valid email" in {
      stubGet("/readOne/test@test.com", 200, s"${Json.obj(
        "firstName" -> cust1.firstName,
        "lastName" -> cust1.lastName,
        "phoneNumber" -> cust1.phoneNumber,
        "email" -> cust1.email,
        "britishCitizen" -> cust1.britishCitizen,
        "dateOfBirth" -> cust1.dateOfBirth)}")
      val result = await(connector.readOne("test@test.com"))
      result shouldBe Some(cust1)
    }
  }

  "PUT /update" should {
    "return a true if accepted response given" in {
      stubPut("/update", 202, "")
      val result = await(connector.update(cust1))
      result shouldBe true
    }
    "return a false if not accepted response given" in {
      stubPut("/update", 406, "")
      val result = await(connector.update(cust1))
      result shouldBe false
    }
    "return a true if bad request response given" in {
      stubPut("/update", 400, "")
      val result = await(connector.update(cust2))
      result shouldBe false
    }
  }

  "DELETE /delete" should {
    "return a true if accepted response given" in {
      stubDelete("/delete/test@test.com", 202, "")
      val result = await(connector.delete("test@test.com"))
      result shouldBe true
    }
    "return a false if not accepted response given" in {
      stubDelete("/delete/test@test.com", 406, "")
      val result = await(connector.delete("test@test.com"))
      result shouldBe false
    }
  }
}
