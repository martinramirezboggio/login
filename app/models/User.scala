package models

import play.api.libs.json.{ Json, OFormat }
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json._


/**
 * Created by martinramirezboggio on 13/10/2020
 */
case class User(
               _id: Option[BSONObjectID],
               email: String,
               password: String,
               firstName: String,
               lastName: String,
               active: Boolean
               )

object User {
  implicit val format: OFormat[User] = Json.format[User]
}

case class LoginCredentials(email: String, password: String)

object LoginCredentials {
  implicit val format: OFormat[LoginCredentials] = Json.format[LoginCredentials]
}
