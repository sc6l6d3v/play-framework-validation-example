package controllers

import models.{ErrorResponse, Person}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, JsResult, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import ErrorResponse._

import scala.collection.mutable

class PersonController extends Controller {

  var personMap = mutable.Map[String, Person]()
  def personById(personId: String): Action[AnyContent] = Action {
    Ok(Json.toJson(personMap(personId)))
  }

  def echo: Action[JsValue] = Action(parse.json) {
    implicit request =>
      val eitherPerson = validateParsedResult(request.body.validate[Person])
      eitherPerson.fold(
        errorResponse => BadRequest(Json.toJson(errorResponse)),
        person => {
          personMap(person.studentId) = person
          Ok(Json.toJson(person))
        }
      )
  }

  def validateParsedResult[T](jsResult: JsResult[T]): Either[ErrorResponse, T] =
    jsResult.fold(
      (errors: Seq[(JsPath, Seq[ValidationError])]) => {
        val map = fmtValidationResults(errors)
        Left(ErrorResponse("Validation Error", map))
      },
      (t: T) => Right(t)
    )
}
