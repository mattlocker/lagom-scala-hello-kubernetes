import sbt._

object Version {
  val constructr = "0.18.1"
  val constructrZooKeeper = "0.4.0"
  val serviceLocatorDns = "2.2.2"
}

object Library {
  val constructr          = "de.heikoseeberger"        %% "constructr"                        % Version.constructr
  val constructrZooKeeper = "com.lightbend.constructr" %% "constructr-coordination-zookeeper" % Version.constructrZooKeeper
  val serviceLocatorDns   = "com.lightbend"            %% "service-locator-dns"  % Version.serviceLocatorDns
}
