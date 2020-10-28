package repositories

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.modules.reactivemongo._
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import models.{LoginCredentials, User}
import reactivemongo.api.commands.{MultiBulkWriteResult, WriteResult}


/**
 * Created by martinramirezboggio on 18/10/2020
 */

class UserRepository @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi){

  private def collection: Future[JSONCollection] = reactiveMongoApi.database.map( _.collection("users"))

  def findByEmail(loginCredentials: LoginCredentials): Future[Option[User]] = {
    collection.flatMap( _. find(BSONDocument("email" -> loginCredentials.email)).one[User])
  }

  def list(limit: Int): Future[Seq[User]] = {
    collection.flatMap( _
      .find(BSONDocument())
      .cursor[User](ReadPreference.primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[User]]())
    )
  }

  def create(users: User): Future[WriteResult] =
    collection.flatMap(_.insert.one(users))

  def read(id: BSONObjectID): Future[Option[User]] = {
    collection.flatMap( _.find(BSONDocument("_id" -> id)).one[User])
  }

  def update(id: BSONObjectID, user: User): Future[Option[User]] = {
    collection.flatMap( _
      .findAndUpdate(
        BSONDocument("_id" -> id),
        BSONDocument(
          f"$$set" -> BSONDocument(
            "email" -> user.email,
            "password" -> user.password,
            "firstName" -> user.firstName,
            "lastName" -> user.lastName,
            "active" -> user.active
          )
        ), fetchNewObject = true
      ).map( _.result[User])
    )
  }

  def destroy(id: BSONObjectID): Future[Option[User]] =
    collection.flatMap( _.findAndRemove(BSONDocument("_id" -> id)).map( _.result[User]))
}
