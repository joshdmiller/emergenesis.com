package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._

import jp.t2v.lab.play20.auth.LoginLogout
import models._
import se.radley.plugin.salat.Formats._
import com.mongodb.casbah.Imports.MongoDBObject

object Application extends Controller with LoginLogout with Authentication {
  
  def index = Action {
    val posts = Post.find(ref = MongoDBObject("is_published" -> true))
      .sort(orderBy = MongoDBObject("modified_at" -> -1))
      .limit(1)
    Ok(views.html.index(posts))
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
  
  val emailForm = Form(
    mapping(
      "email" -> email,
      "name" -> nonEmptyText,
      "subject" -> nonEmptyText,
      "message" -> nonEmptyText
    )(Message.apply)(Message.unapply)
  )
  
  def contact = Action { implicit request =>
    Ok(views.html.contact(emailForm))
  }

  def sendemail = Action { implicit request =>
    emailForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.contact(formWithErrors)),
      msg => {
        Email.send(msg)
        Redirect(routes.Application.contact).flashing("success" -> "Message sent!")
      }
    )
  }
}
