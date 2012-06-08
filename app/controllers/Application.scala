package controllers

import play.api._
import play.api.mvc._

import models._
import views._

object Application extends Controller {
    /**
     * Home page
     */
    def index = Action {
        Ok(html.index())
    }
}

