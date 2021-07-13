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

package uk.gov.hmrc.agentsfrontend.models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, text}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.{Json, OFormat}

import scala.util.matching.Regex

case class Agent(name: String)

case class AgentClient(arn: String, crn: String)

object AgentClient{
  implicit val format: OFormat[AgentClient] = Json.format[AgentClient]
}

case class ClientCode(crn: String)

object ClientCode {
  val form: Form[ClientCode] = Form (
    mapping(
      "crn" -> nonEmptyText
    )(ClientCode.apply)(ClientCode.unapply)
  )
}

case class ContactNumber(number: String)

object ContactNumber {
  val validNumber: Regex = """^[0][1-9]\d{9}$|^[1-9]\d{9}$""".r

  val valid: Constraint[String] = Constraint("constraints.number")({ plainText =>
    val errors = plainText match {
      case validNumber() => Nil
      case _ => Seq(ValidationError("Please Enter a 10-11 digit, UK Phone Number e.g 07123456789"))
    }
    if (errors.isEmpty) {
      Valid
    } else {
      Invalid(errors)
    }
  })

  val contactForm: Form[ContactNumber] =
    Form(
      mapping(
        "number" -> text.verifying(valid)
      )(ContactNumber.apply)(ContactNumber.unapply))
}

case class AgentDetails(arn: String, businessName: String, email: String, mobileNumber: Long, moc: Seq[String], propertyNumber: String, postcode: String)

object AgentDetails {
  implicit val format: OFormat[AgentDetails] = Json.format[AgentDetails]
}




case class Address(propertyNumber: String, postcode: String) {
  val encode: String = propertyNumber + "/" + postcode
}

object Address {

  val regex: Regex = """(?:[A-Za-z]\d ?\d[A-Za-z]{2})|(?:[A-Za-z][A-Za-z\d]\d ?\d[A-Za-z]{2})|(?:[A-Za-z]{2}\d{2} ?\d[A-Za-z]{2})|(?:[A-Za-z]\d[A-Za-z] ?\d[A-Za-z]{2})|(?:[A-Za-z]{2}\d[A-Za-z] ?\d[A-Za-z]{2})""".stripMargin.r

  val postcodeCheckConstraint: Constraint[String] = Constraint("constraints.postcodecheck")({ plainText =>
    val errors = plainText match {
      case regex() => Nil
      case _ => Seq(ValidationError("Input is not a valid postcode"))
    }
    if (errors.isEmpty) {
      Valid
    } else {
      Invalid(errors)
    }
  })

  val addressForm: Form[Address] =
    Form(
      mapping(
        "propertyNumber" -> nonEmptyText,
        "postcode" -> text.verifying(postcodeCheckConstraint)
      )(Address.apply)(Address.unapply))

  def decode(string: String): Address = {
    val (n, p): (String, String) = string.split("/").toList match {
      case h :: t :: _ => h -> t
      case _ => ("", "")
    }
    Address(n, p)
  }
}