# j2me-gamedev-series

Learn Game Development on J2ME

### Running slideshows

The slides are built using [Marp](https://github.com/marp-team/marp-cli),
which is Node.js based. We're using a gradle plugin to automatically
download Node.js behind the scenes and use it to run Marp. 

Use the following command to start slideshow:

```
./gradlew :slideshow
```

By default, the UI should come live on [port 8080](http://localhost:8080/).
