package controllers

import play.api._
import play.api.mvc._
import jp.t2v.lab.play20.auth._

import models._

object Blog extends Controller with Auth with Authentication {
  
  def index(num: Integer = 1) = optionalUserAction { maybeUser => request =>
    val limit = 5
    val posts = Post.findAll.skip((num-1)*limit).limit(limit)
    val is_prev = num > 1
    val is_next = Post.count > limit * num
    val is_auth = maybeUser match {
        case Some(user) => true
        case None => false
    }
    Ok(views.html.blog(posts, num, is_next, is_prev, is_auth))
  }

  def show(slug: String) = Action {
    Post.findOneBySlug(slug) match {
        case Some(post) => {
            Ok(views.html.blogPost(post))
        }

        case None => {
            NotFound("That post does not exist.")
        }
    }

  }

  //def add = authorizedAction(Administrator) { user => implicit request =>
  //}
  def add = TODO

  //def create = authorizedAction(Administrator) { user => implicit request =>
  //}
  def create = TODO
  
  def rss = TODO
  
}
