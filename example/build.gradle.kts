plugins {
    id("j2me-app")
}

tasks.jar {
    manifest {
        attributes["MIDlet-1"] = "Example, , game.ExampleApp" // "name, icon, entrypoint"
        attributes["MicroEdition-Profile"] = "MIDP-2.0"
        attributes["MicroEdition-Configuration"] = "CLDC-1.1"

        attributes["MIDlet-Name"] = "Example"
        attributes["MIDlet-Version"] = "0.1.0"
        attributes["MIDlet-Vendor"] = "you"
    }
}
