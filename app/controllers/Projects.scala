package controllers

import play.api._
import play.api.mvc._

object Projects extends Controller {
  
  def index = Action {
    Ok(views.html.projects())
  }
  
  def citeplasm = Action {
    Ok(views.html.citeplasm())
  }

  def bps = TODO
  def semanticweb = TODO
  
}
