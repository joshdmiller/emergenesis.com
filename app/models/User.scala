package models

import java.util.Date

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class User(id: Long, fullname: String, username: String, password: String)

object User {
    val simple = {
        get[Long]("user_id") ~
        get[String]("fullname") ~
        get[String]("username") ~
        get[String]("password") map {
            case user_id~fullname~username~password => User(user_id, fullname, username, password)
        }
    }
  
  /**
   * Retrieve a User from username.
   */
  def findByUsername(username: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where username = {username}").on(
        'username -> username
      ).as(User.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(username: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from users where 
         username = {username} and password = {password}
        """
      ).on(
        'username -> username,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
}

