package controllers

import play.api._
import play.api.mvc._

object Projects extends Controller {
  
  def index = Action {
    Ok(views.html.projects())
  }
  
  def citeplasm = TODO
  def bps = TODO
  def semanticweb = TODO
  
}
