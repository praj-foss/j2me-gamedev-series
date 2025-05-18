plugins {
    id("j2me-app")
}

tasks.jar {
    manifest {
        attributes["MIDlet-1"] = "Example, , game.ExampleApp"
        // value contains: name, icon, entrypoint
    }
}
