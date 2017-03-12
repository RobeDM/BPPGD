name := "ppackubuntu"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

resolvers += "Repo at github.com/ankurdave/maven-repo" at "https://raw.githubusercontent.com/ankurdave/maven-repo/master"

libraryDependencies ++=  Seq(
  "org.apache.spark" %% "spark-core" % "2.0.2",
  "org.apache.spark"  %% "spark-mllib"             % "2.0.2",
  "amplab" % "spark-indexedrdd" % "0.4.0",
  "com.ankurdave" % "part_2.10" % "0.1",  // artifact is not published for 2.11, but it only contains Java code anyway
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
)