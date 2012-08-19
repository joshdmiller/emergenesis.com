// From Twitter's standard-project
package controllers

import scala.collection.JavaConversions._

trait Environmentalist {
  val environment = mapAsScalaMap(System.getenv())
}
