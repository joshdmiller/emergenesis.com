package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

case class Account(
  id: ObjectId = new ObjectId,
  email: String,
  pass: String
)

object Account extends ModelCompanion[Account, ObjectId] {
  val collection = mongoCollection("accounts")
  val dao = new SalatDAO[Account, ObjectId](collection = collection) {}

  def findByEmail(email: String): Option[Account] = dao.findOne(MongoDBObject("email" -> email))

  def authenticate(email: String, pass: String) = {
    dao.findOne(MongoDBObject("email" -> email, "pass" -> pass))
  }
}

