plugins {
    java
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("test")
        resources.srcDir("test-resources")
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_3
    sourceCompatibility = JavaVersion.VERSION_1_3
}

// this will create a separate group of dependencies
val emulation by configurations.creating {
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

tasks.register<JavaExec>("emulate") {
    classpath(emulation.files)
    mainClass.set("org.microemu.app.Main")
    argumentProviders.add(CommandLineArgumentProvider {
        tasks.jar.map {
            listOf(it.archiveFile.get().toString())
        }.get()
    })
    dependsOn(tasks.jar)
}
