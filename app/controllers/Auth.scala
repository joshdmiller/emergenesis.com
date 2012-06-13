package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._
import models._

object Auth extends Controller {
    val loginForm = Form(
        tuple(
            "username" -> text,
            "password" -> text
        ) verifying ("Invalid username or password", result => result match {
            case (username, password) => User.authenticate(username, password).isDefined
        })
    )

    def login = Action { implicit request =>
        Ok(html.login(loginForm))
    }

    def authenticate = Action { implicit request =>
        loginForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.login(formWithErrors)),
            user => Redirect(routes.Application.admin(None)).withSession(Security.username -> user._1)
        )
    }

    def logout = Action {
        Redirect(routes.Auth.login).withNewSession.flashing(
            "success" -> "You are now logged out."
        )
    }
}

