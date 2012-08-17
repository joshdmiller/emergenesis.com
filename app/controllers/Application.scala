package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._

import jp.t2v.lab.play20.auth.LoginLogout
import models._

object Application extends Controller with LoginLogout with Authentication {
  
  def index = Action {
    Ok(views.html.index())
  }

  def about = Action {
    Ok(views.html.about())
  }
  def projects = Action {
    Ok(views.html.projects())
  }

  val loginForm = Form {
    mapping("email" -> text, "pass" -> text)(Account.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /** 
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `PlainResult`, 
   * you can add a procedure like the following.
   * 
   *   gotoLogoutSucceeded.flashing(
   *     "success" -> "You've been logged out"
   *   )
   */
  def logout = Action { implicit request =>
    gotoLogoutSucceeded.flashing(
        "success" -> "You've been logged out."
    )
  }

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   * 
   * Since the `gotoLoginSucceeded` returns `PlainResult`, 
   * you can add a procedure like the `gotoLogoutSucceeded`.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => gotoLoginSucceeded(user.get.email).flashing(
        "success" -> "Login successful."
      )
    )
  }
  
  
  def contact = TODO
}
