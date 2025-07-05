plugins {
    java
}

sourceSets {
    main {
        java.setSrcDirs(setOf("src"))
        resources.setSrcDirs(setOf("resources"))
    }
    test {
        java.setSrcDirs(setOf("test"))
        resources.setSrcDirs(setOf("test-resources"))
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_3
    sourceCompatibility = JavaVersion.VERSION_1_3
}

tasks.compileJava {
    // suppress warnings about obsolete java version 1.3
    options.compilerArgs.add("-Xlint:-options")
}

/**
 * Defines a group of dependencies called "emulation".
 *
 * Gradle will keep it separate from our project dependencies,
 * and use it only for the "emulate" task.
 */
val emulation = configurations.create("emulation") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.microemu:midpapi20:2.0.4")
    emulation("org.microemu:microemulator:2.0.4")
}

/**
 * Defines a new "emulate" task for current project.
 *
 * It has the same effect as this command:
 *    java -jar microemulator.jar /path/to/game.jar
 */
tasks.register<JavaExec>("emulate") {
    classpath(emulation.files)
    mainClass.set("org.microemu.app.Main")
    argumentProviders.add(CommandLineArgumentProvider {
        listOf(
            tasks.jar.flatMap { it.archiveFile }.get().toString()
            // todo: pass custom device xml with --device
        )
    })
    dependsOn(tasks.jar)
}
