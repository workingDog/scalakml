
pomExtra := {
  <scm>
    <url>https://github.com/workingDog/scalakml</url>
    <connection>scm:git:git@github.com:workingDog/scalakml.git</connection>
  </scm>
    <developers>
      <developer>
        <id>workingDog</id>
        <name>Ringo Wathelet</name>
        <url>https://github.com/workingDog</url>
      </developer>
    </developers>
}

pomIncludeRepository := { _ => false }

publishMavenStyle := true

publishArtifact in Test := false

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

sonatypeProfileName := "com.github.workingDog"
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseTagName := (version in ThisBuild).value

// must do this
//credentials += Credentials("Sonatype Nexus Repository Manager",
//  "oss.sonatype.org",
//  "workingDog",
//  "the password here, not the passphrase")
