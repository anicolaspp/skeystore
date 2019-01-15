name := "skeystore"

organization := "com.github.anicolaspp"

version := "1.0.0"

crossScalaVersions := Seq("2.11.8", "2.12.8")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test" ,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
)

assemblyJarName := s"${name.value}-${scalaVersion.value}-${version.value}.jar"

parallelExecution in (test) := false