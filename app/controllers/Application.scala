package controllers

import play.api._
import play.api.mvc._

import views._
import models._

object Application extends Controller with Secured {
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
     Admin
     */
    def admin( page: Option[Long] ) = IsAuthenticated { username => implicit request =>
        val rpp = 25
        var pn: Long = 1
        val count = Post.count()
        if ( !page.isEmpty ) {
            if ( ((page.get - 1) * rpp) + 1 <= count ) {
                pn = page.get
            }
        }

        val posts = Post.all( rpp, (pn - 1) * rpp )
        Ok(html.admin( posts, pn, rpp, count ))
    }

    /**
     TODO
     */
    def contact = TODO
    def about = TODO
    def projects = TODO
    def newpost = TODO
    def edit( id: Long ) = TODO
    def delete( id: Long ) = TODO
}

/**
 * Provide security features
 */
trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("username")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}

