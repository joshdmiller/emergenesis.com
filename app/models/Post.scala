package models

import java.util.Date

import play.api.db._
import play.api.mvc._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import anorm._
import anorm.SqlParser._
import views._
import models._

case class Post(id: Long, title: String, slug: String, modified_at: Date, body: String, author: String)

object Post {
    val post = {
        get[Long]("post_id") ~ 
        get[String]("title") ~
        get[String]("slug") ~
        get[Date]("modified_at") ~
        get[String]("body") ~
        get[String]("author") map {
            case post_id~title~slug~modified_at~body~author => Post(post_id, title, slug, modified_at, body, author)
        }
    }

    def all(limit: Long, offset: Long): List[Post] = DB.withConnection { implicit c =>
        SQL("SELECT p.*, u.fullname as author FROM posts p INNER JOIN users u ON p.user_id = u.user_id ORDER BY modified_at DESC LIMIT {limit} OFFSET {offset}").on("limit" -> limit, "offset" -> offset).as(post *)
    }

    def count(): Long = DB.withConnection { implicit c =>
        SQL("SELECT count(*) as c FROM posts").apply().head[Long]("c")
    }

    def create(title: String, slug: String, body: String, author: Long): Option[Long] = DB.withConnection { implicit c =>
        SQL("INSERT INTO posts (title, slug, modified_at, body, user_id) VALUES ({t}, {s}, {m}, {b}, {u})")
            .on("t" -> title, "s" -> slug, "m" -> new Date(), "b" -> body, "u" -> author)
            .executeInsert()
    }
}
