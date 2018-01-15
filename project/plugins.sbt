import sbt.Defaults.sbtPluginExtra

resolvers ++= Seq(
  Resolver.url("bintray.scaffolding-plugin.resolver", url("http://dl.bintray.com/fabszn/sbt-plugins"))(Resolver.ivyStylePatterns),
  Resolver.url("bintray.lagom-sbt-plugin.resolver", url("https://bintray.com/lagom/sbt-plugin-releases/lagom-sbt-plugin"))(Resolver.ivyStylePatterns),
  "Sonatype Releases Repository" at "https://oss.sonatype.org/content/repositories/releases"
)

// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.4.0-RC1")
// Needed for checking the scala style
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
// Needed for computing the test coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
// Improved error messages
addSbtPlugin("org.duhemm" % "sbt-errors-summary" % "0.6.0")
// needed for docker packaging
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")

libraryDependencies ++= {
  val sbtV = (sbtBinaryVersion in update).value
  val scalaV = (scalaBinaryVersion in update).value
  val sbtConductRPlugin = sbtPluginExtra("com.lightbend.conductr" % "sbt-conductr" % "2.5.1", sbtV, scalaV)
  val additionalPlugins: Seq[ModuleID] =
    sys.props.get("buildTarget") match {
      case Some(v) if v.toLowerCase == "conductr" => Seq(sbtConductRPlugin)
      case None => Seq(sbtConductRPlugin)
      case _ => Seq.empty
    }
  additionalPlugins
}