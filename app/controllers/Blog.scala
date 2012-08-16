package controllers

import play.api._
import play.api.mvc._

import models.Post

object Blog extends Controller {
  
  def index(num: Integer = 1) = Action {
    val limit = 5
    val posts = Post.findAll.skip((num-1)*limit).limit(limit)
    val is_prev = num > 1
    val is_next = Post.count > limit * num
    Ok(views.html.blog(posts, num, is_next, is_prev))
  }

  def show(slug: String) = TODO
  
  def rss = TODO
  
}
