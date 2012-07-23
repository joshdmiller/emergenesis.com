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

  def foundation = Action {
    Ok(views.html.foundation())
  }

  def opensource = Action {
    Ok(views.html.opensource())
  }

  def contact = TODO
  
  def legal = TODO
  
}
