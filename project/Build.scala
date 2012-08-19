import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "emergenesiscom"
    val appVersion      = "1.0"
    
    val appDependencies = Seq(
      "se.radley" %% "play-plugins-salat" % "1.0.8",
      "jp.t2v" %% "play20.auth" % "0.3-SNAPSHOT",
      "javax.mail" % "mail" % "1.4.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId"
    ) dependsOn(
      RootProject(uri("git://github.com/tristanjuricek/knockoff.git"))
    )

}
