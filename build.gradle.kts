import com.github.gradle.node.npm.task.NpxTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download.set(true)
    version.set("18.17.1")
}

/**
 * Defines a new "slideshow" task for the root project.
 *
 * It works just like the following command:
 *    npx @marp-team/marp-cli@4.1.2 -s .
 */
tasks.register<NpxTask>("slideshow") {
    command.set("@marp-team/marp-cli@4.1.2")
    args.set(listOf("-s", "."))
//    environment.set(mapOf("PORT" to "9090")) // for a custom port
}
