package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

case class Post(
  id: ObjectId = new ObjectId,
  title: String,
  slug: String,
  author: String,
  body: String,
  is_published: Boolean,
  modified_at: Date = new Date()
)

object Post extends ModelCompanion[Post, ObjectId] {
  val collection = mongoCollection("posts")
  val dao = new SalatDAO[Post, ObjectId](collection = collection) {}

  def findOneBySlug(slug: String): Option[Post] = dao.findOne(MongoDBObject("slug" -> slug))

  def count: Long = dao.collection.count

  def createOrSave(
    id: Option[ObjectId],
    title: String, 
    slug: String, 
    author: String, 
    body: String,
    is_published: Boolean
  ) = {
    Post(
      id getOrElse new ObjectId,
      title,
      slug,
      author,
      body,
      is_published,
      new Date()
    )
  }

  def toForm(post: Post) = Some(
    Some(post.id),
    post.title,
    post.slug,
    post.author,
    post.body,
    post.is_published
  )
}

