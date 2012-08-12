package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def about = Action {
    Ok(views.html.about())
  }
  def projects = Action {
    Ok(views.html.projects())
  }
  
  def contact = TODO
}
