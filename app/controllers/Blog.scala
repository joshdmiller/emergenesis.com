package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import jp.t2v.lab.play20.auth._
import se.radley.plugin.salat.Formats._
import com.mongodb.casbah.Imports.MongoDBObject

import models._
import views.html

object Blog extends Controller with Auth with Authentication {

  val editForm = Form(
    mapping(
      "id" -> optional(objectId),
      "title" -> nonEmptyText,
      "slug" -> nonEmptyText,
      "author" -> nonEmptyText,
      "body" -> nonEmptyText,
      "is_published" -> boolean
    )(Post.createOrSave)(Post.toForm)
  )
  
  def list(num: Integer = 1) = optionalUserAction { maybeUser => implicit request =>
    val limit = 5
    val posts = maybeUser match {
      case Some(user) => Post.findAll
      case None => Post.find(ref = MongoDBObject("is_published" -> true))
    }

    posts.sort(orderBy = MongoDBObject("modified_at" -> -1))
      .skip((num-1)*limit)
      .limit(limit)
    val is_prev = num > 1
    val is_next = Post.count > limit * num
    val is_auth = maybeUser match { case Some(user) => true case None => false }
    Ok(html.blog.list(posts, num, is_next, is_prev, is_auth))
  }

  def show(slug: String) = optionalUserAction { maybeUser => implicit request =>
    Post.findOneBySlug(slug) match {
        case Some(post) => {
            val is_auth = maybeUser match { case Some(user) => true case None => false }
            Ok(html.blog.show(post, is_auth))
        }

        case None => NotFound("That post does not exist.")
    }

  }

  def add = authorizedAction(Administrator) { user => implicit request =>
    Ok(html.blog.edit(editForm))
  }

  def submit = authorizedAction(Administrator) { user => implicit request =>
    editForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.blog.edit(formWithErrors)),
      post => {
        Post.save(post)
        Redirect(routes.Blog.show(post.slug)).flashing(
            "success" -> "Post created"
        )
      }
    )
  }
  
  def delete(slug: String) = authorizedAction(Administrator) { user => implicit request =>
    Post.findOneBySlug(slug) match {
      case Some(post) => {
        Post.remove(post)
        Redirect(routes.Blog.list(1)).flashing("success" -> "Post deleted.")
      }
      case None => NotFound("Cannot delete a nonexistent post.")
    }
  }
  
  def edit(slug: String) = authorizedAction(Administrator) { user => implicit request =>
    Post.findOneBySlug(slug) match {
      case Some(post) => {
        Ok(html.blog.edit(editForm.fill(post)))
      }

      case None => NotFound("Cannot edit a nonexistent post.")
    }
  }
  
  def rss = TODO
  
}
