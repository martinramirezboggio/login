package controllers

import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{ExecutionContext, Future}
import repositories.UserRepository
import models.{LoginCredentials, User}
import services.UserService

/**
 * Created by martinramirezboggio on 18/10/2020
 */
@Singleton
class UserController @Inject()(
                                implicit val ec: ExecutionContext,
                               val controllerComponents: ControllerComponents,
                              userService: UserService
                              ) extends BaseController{

  def list: Action[AnyContent] = Action.async { implicit request =>
    if(request.session.isEmpty)
     Future.successful(Unauthorized(Json.obj("message" -> "You are not authenticated")))
    else {
      userService.list().map { users =>
        Ok(Json.toJson(users))
      }
    }
  }

  def create: Action[JsValue] = Action.async(parse.json){
    _.body
      .validate[User]
      .map { user =>
        userService.create(user).map { _ =>
          Ok(Json.obj("status" -> CREATED, "message" -> "User created successfully"))
        }
      }
      .getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  def read(id: BSONObjectID): Action[AnyContent] = Action.async { implicit request =>
    if(request.session.isEmpty)
      Future.successful(Unauthorized("You are not authenticated"))
    else {
      userService.read(id).map { maybeUser =>
        maybeUser.map { user =>
          Ok(Json.toJson(user))
        }.getOrElse(NotFound)
      }
    }
  }

  def update(id: BSONObjectID): Action[JsValue] = Action.async(parse.json) { implicit request =>
    if(request.session.isEmpty)
      Future.successful(Unauthorized("You are not authenticated"))
    else {
      request.body
        .validate[User]
        .map { user =>
          userService.update(id, user).map { _ =>
            Ok(Json.obj("status" -> ACCEPTED, "message" -> "User updated successfully"))
          }
        }
        .getOrElse(Future.successful(BadRequest("Invalid format")))
    }
  }

  def authenticate: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[LoginCredentials]
      .map{ loginCredentials =>
        userService.authenticate(loginCredentials).map {
          case Some(user) => Ok(Json.obj("status" -> ACCEPTED, "message" -> "user logged")).withSession(
            request.session + ("email" -> user.email))
          case _ => BadRequest(Json.obj("message" -> "Wrong user credentials"))
        }
      }
      .getOrElse(Future.successful(BadRequest("Invalid Format")))
  }

  def signOut: Action[AnyContent] = Action {
    Ok(Json.obj("message" -> "Sign out successful")).withNewSession
  }
}
