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
To use a different port, try with environment variable `PORT` set to the target value.

## Creating new projects

To create a new project, follow these steps:

1. Create a new directory (for example: `pong`). This name will become your project name.
2. Include project name in `settings.gradle.kts` file (adding this: `include("pong")`).
3. Copy the contents from `example` directory to this.
4. Run it once for verification (using command: `./gradlew :pong:emulate`).
5. Sip your chai/coffee.

## Generating thumbnails

For the sake of automation, there is also a gradle task to generate YouTube video thumbnails.
Use the following command to run this task:

```
./gradlew :thumbnail -Pepisode=25 -PscaleFactor=2
```

Here, `scaleFactor` property is optional and is set to `10` by default.
The `episode` property is mandatory and must be a number within `0` to `99`.

The generated thumbnail should look like this:

![](/2025/thumbnails/example.png)


## Thanks! 👋
