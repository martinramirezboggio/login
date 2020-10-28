package services

import javax.inject.Inject
import models.{LoginCredentials, User}
import org.mindrot.jbcrypt.BCrypt
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.{MultiBulkWriteResult, WriteResult}
import reactivemongo.bson.BSONObjectID
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by martinramirezboggio on 18/10/2020
 */

class UserService @Inject()(implicit ec: ExecutionContext, userRepository: UserRepository){

  def authenticate(loginCredentials: LoginCredentials): Future[Option[User]] = {
    (
      for {
        Some(user) <- userRepository.findByEmail(loginCredentials) if BCrypt.checkpw(loginCredentials.password, user.password)
      } yield Some(user)
    ) recover{
      case _ => None
    }
  }

  def list(limit: Int =100): Future[Seq[User]] = userRepository.list(limit)

  def create(user: User): Future[WriteResult] = {
    val updatedUser = user.copy(password = BCrypt.hashpw(user.password, BCrypt.gensalt()))
    userRepository.create(updatedUser)
  }

  def read(id: BSONObjectID): Future[Option[User]] = userRepository.read(id)

  def update(id: BSONObjectID, user: User): Future[Option[User]] = userRepository.update(id, user)
}
