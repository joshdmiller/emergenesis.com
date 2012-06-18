package controllers

import play.api._
import play.api.mvc._
import play.api.libs.Files._

import views._
import models._

import util.control.Breaks._

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
    def admin( page: Option[Long] = None ) = IsAuthenticated { username => implicit request =>
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
    
    def newpost = IsAuthenticated { username => implicit request =>
        Ok(html.newpost())
    }

    def upload = IsMultipartAuthenticated(parse.multipartFormData) { username => implicit request =>
        request.body.file("file").map { postfile =>
            import java.io.{File,BufferedInputStream,DataInputStream,FileInputStream}
            val filename = postfile.filename 
            val contentType = postfile.contentType

            val fis = new FileInputStream(postfile.ref.file)
            val bis = new BufferedInputStream(fis)
            val dis = new DataInputStream(bis)
            var keys = scala.collection.mutable.HashMap.empty[String,String]
            var body = ""

            var currentLine = 1
            var errorMessage = ""
            var inHeader = false
            breakable { while ( dis.available() != 0 ) {
                val line = dis.readLine()
                if ( currentLine == 1 ) {
                    if (line != "---" ) {
                        errorMessage = "Malformed document. Must have YAML metadata at top."
                        break
                    }

                    inHeader = true
                } else {
                    if ( inHeader ) {
                        if ( line == "---" ) {
                            inHeader = false
                        } else {
                            val KeyValue = """^(\w+): "{0,1}([a-zA-Z0-9\-_'\\/&\? \.,:;\[\]\{\}\(\)]+)"{0,1}$""".r
                            val KeyValue(key, value) = line
                            keys += (key -> value)
                        }
                    } else {
                        body += line + "\n"
                    }
                }

                currentLine += 1
            } }

            if ( ! keys.contains("title") ) {
                errorMessage = "Malformed document. YAML must include a 'title' field."
            }

            if ( errorMessage != "" ) {
                Redirect(routes.Application.admin(None)).flashing(
                    "error" -> errorMessage
                )
            } else {
                if (!keys.contains("author_id")) {
                    keys += ("author_id" -> "1")
                }

                if (!keys.contains("slug")) {
                    val title = keys.getOrElse("title", "").trim.toLowerCase
                    val unsafeChars = """[ '_\\/\.,:;\[\]\{\}\(\)&\?]+""".r
                    val slug = unsafeChars.replaceAllIn(title, "-")
                    keys += ("slug" -> slug)
                }

                val id: Option[Long] = Post.create(
                    keys.getOrElse("title", ""),
                    keys.getOrElse("slug", ""),
                    body,
                    keys.getOrElse("author", "").toLong
                )

                id match {
                    case None => {
                        Redirect(routes.Application.admin(None)).flashing(
                            "error" -> "Could not persist to database."
                        )
                    }
                    case Some(lid) => {
                        Redirect(routes.Application.admin(None)).flashing(
                            "success" -> "Post created!"
                        )
                    }
                }
            }
        }.getOrElse {
            Redirect(routes.Application.admin(None)).flashing(
                "error" -> "Missing file"
            )
        }
    }

    def delete( id: Long ) = IsAuthenticated { username => implicit request =>
        val post = Post.find(id)
        Ok(html.deletepost(post))
    }

    def deleteconfirm( id: Long ) = IsAuthenticated { username => implicit request =>
        Post.delete(id) match {
            case 1 => {
                Redirect(routes.Application.admin(None)).flashing(
                    "success" -> "Post deleted."
                )
            }
            case _ => {
                Redirect(routes.Application.admin(None)).flashing(
                    "error" -> "Something went wrong."
                )
            }
        }
    }
    
    def show( slug: String ) = Action { request =>
        Post.findBySlug(slug) match {
            case Some(p) => {
                Ok(html.show(p))
            }
            case None => {
                NotFound("Post does not exist.")
            }
        }
    }
    
    /**
     TODO
     */
    def contact = TODO
    def about = TODO
    def projects = TODO
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

  def IsMultipartAuthenticated(p: BodyParser[MultipartFormData[TemporaryFile]])(f: => String => 
  Request[MultipartFormData[TemporaryFile]] => Result) = 
  Security.Authenticated(username, onUnauthorized) { user => 
    Action(p)(request => f(user)(request)) 
  } 
}

