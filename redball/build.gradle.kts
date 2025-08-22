plugins {
    id("j2me-app")
}

tasks.jar {
    manifest {
        attributes["MIDlet-1"] = "RedBall, , game.BouncingBall" // "name, icon, entrypoint"
        attributes["MicroEdition-Profile"] = "MIDP-2.0"
        attributes["MicroEdition-Configuration"] = "CLDC-1.1"

        attributes["MIDlet-Name"] = "RedBall"
        attributes["MIDlet-Version"] = "0.1.0"
        attributes["MIDlet-Vendor"] = "Home"
    }
}
