package controllers

import play.api._
import play.api.mvc._

object Blog extends Controller {
  
  def index = Action {
    Ok(views.html.blog())
  }

  def show(slug: String) = TODO
  
  def rss = TODO
  
}
