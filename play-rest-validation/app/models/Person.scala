package models

import models.Gender.Gender
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Person(firstName: String, lastName: String, studentId: String, gender: Gender)

object Person {
  val nameValError = "must be more than 2 characters"
  val idValError = "must be 10 digits"
  val digitCheckError = "must be a number"

  // #EasyWayOut
  // implicit val jsonFormat = Json.format[Person]

  implicit val jsonWrites = Json.writes[Person]
  implicit val jsonValidatedReads = (
    (JsPath \ "firstName").read[String]   //vanilla read followed by additional validators
      .filter(ValidationError(nameValError))(nameLen(_)) and

      (JsPath \ "lastName").read[String]
        .filter(ValidationError(nameValError))(x => nameLen(x)) and

      (JsPath \ "studentId").read[String]
        .filter(ValidationError(idValError))(number => digLen(number))
        .filter(ValidationError(digitCheckError))(number => number.forall(Character.isDigit)) and

      (JsPath \ "gender").read[Gender]

    ) (Person.apply _)

  val nameLen = (name:String) => name.length > 2

  val digLen = (digits: String) => digits.length == 10
}
