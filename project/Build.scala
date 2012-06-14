import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "emergenesis-com"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "eu.henkelmann" %% "actuarius" % "0.2.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Markdown processor, Actuarius
      resolvers += "Christophs Maven Repo" at "http://maven.henkelmann.eu/"
    )

}
