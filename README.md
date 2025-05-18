# j2me-gamedev-series

Learn Game Development on J2ME. Playlist is available 
[on YouTube](https://youtube.com/playlist?list=PLkZEhGAcd_8kK842RW7nr739FRRKqiE3S) 

## Emulating apps

Applying the `j2me-app` gradle plugin will provide an `emulate` task.
This uses [MicroEmulator](https://github.com/barteo/microemu/tree/master/microemulator#readme) 
to emulate the generated JAR file. 

Here's how to emulate the `example/` project:

```
./gradlew :example:emulate
```

## Running slideshows

The slides are built using [Marp](https://github.com/marp-team/marp-cli),
which is Node.js based. We're using a gradle plugin to automatically
download Node.js behind the scenes and use it to run Marp. 

Use the following command to start slideshow:

```
./gradlew :slideshow
```

By default, the UI should come live on [port 8080](http://localhost:8080/).

## Thanks! 👋
