import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
import sbt.Keys.version
import sbt.Resolver.bintrayRepo

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val buildVersion = sys.props.getOrElse("buildVersion", "1.0.0-SNAPSHOT")

lazy val `hello` = (project in file("."))
  .aggregate(`hello-api`, `hello-impl`)

lazy val `hello-api` = (project in file("hello-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `hello-impl` = (project in file("hello-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    ),
    version := buildVersion,
    version in Docker := buildVersion,
    dockerBaseImage := "openjdk:8-jre-alpine",
    packageName := "hello-impl", // sets the docker image name
    dockerRepository := Some(BuildTarget.dockerRepository),
    dockerUpdateLatest := true,
    dockerEntrypoint ++= """-Dhttp.address="$(eval "echo $HELLOSERVICE_BIND_IP")" -Dhttp.port="$(eval "echo $HELLOSERVICE_BIND_PORT")" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")" -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://helloservice@$NODE"; I=$(expr $I + 1); done)""".split(" ").toSeq,
    dockerCommands :=
      dockerCommands.value.flatMap {
        case ExecCmd("ENTRYPOINT", args @ _*) => Seq(Cmd("ENTRYPOINT", args.mkString(" ")))
        case c @ Cmd("FROM", _) => Seq(c, ExecCmd("RUN", "/bin/sh", "-c", "apk add --no-cache bash && ln -sf /bin/bash /bin/sh"))
        case v => Seq(v)
      },
    resolvers += bintrayRepo("hajile", "maven"),
    resolvers += bintrayRepo("hseeberger", "maven")
  )
  .settings(BuildTarget.additionalSettings)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`hello-api`)