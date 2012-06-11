package controllers

import play.api._
import play.api.mvc._

import views._
import models._

object Application extends Controller {
    /**
     * Home page
     */
    def index = Action {
        Ok(html.index())
    }

    /** 
     Blog 
     */
    def blog( page: Option[Long] ) = Action {
        val rpp = 5
        var pn: Long = 1
        val count = Post.count()
        if ( !page.isEmpty ) {
            if ( ((page.get - 1) * rpp) + 1 <= count ) {
                pn = page.get
            }
        }

        val posts = Post.all( rpp, (pn - 1) * rpp )
        Ok(html.blog( posts, pn, rpp, count ))
    }

    /**
     TODO
     */
    def contact = TODO
    def about = TODO
    def projects = TODO
}

