plugins {
    id("ch.essmann.gradle.check-java-version") version "1"
}

checkJavaVersion {
    maximumJavaVersion.set(JavaLanguageVersion.of(8))
}

rootProject.name = "j2me-gamedev-series"

include("example")
include("redball")
