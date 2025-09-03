import com.github.gradle.node.npm.task.NpxTask
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

val episode = providers.gradleProperty("episode")
val scaleFactor = providers.gradleProperty("scaleFactor").orElse("10")
val thumbnailFile = project.layout.buildDirectory.file("thumbnail.png")

data class Point(val x: Int, val y: Int)


/**
 * Task to start slideshow using Marp CLI.
 * Use it like:
 *    PORT=8080 ./gradlew :slideshow
 *
 * It works just like the following command:
 *    npx @marp-team/marp-cli@4.1.2 -s .
 */
tasks.register<NpxTask>("slideshow") {
    command.set("@marp-team/marp-cli@4.1.2")
    args.set(listOf("-s", "."))
}
node {
    download.set(true)
    version.set("18.17.1")
}


/**
 * Task to create thumbnail for YouTube videos.
 * Use it like:
 *    ./gradlew :thumbnail -Pepisode=42 -PscaleFactor=16
 */
tasks.register("thumbnail") {
    if (!episode.isPresent) throw GradleException("""
        This task needs property 'episode' to be set.
        For example, try again with: -Pepisode=69
        """.trimIndent())

    inputs.properties("episode" to episode, "scaleFactor" to scaleFactor)
    outputs.file(thumbnailFile)

    val ep = episode.map { it.toInt() }.filter { it in 0..99 }.orNull
        ?: throw GradleException("Property 'episode' must be a number within 0 to 99")

    val sf = scaleFactor.map { it.toInt() }.filter { it in 1..20 }.orNull
        ?: throw GradleException("Property 'scaleFactor' must be a number within 1 to 20")

    doLast {
        // read source images
        val bg = ImageIO.read(project.file("2025/thumbnails/bg.png"))
        val digits = ImageIO.read(project.file("2025/thumbnails/digits.png"))
        logger.lifecycle("Generating thumbnail for episode {} with dimensions {}x{}",
            ep, sf*bg.width, sf*bg.height)

        // prepare for drawing digits
        val merged = BufferedImage(bg.width, bg.height, BufferedImage.TYPE_INT_RGB)
        val g = merged.createGraphics()

        val size = Point(16, 12)                        // width and height for each digit

        fun drawDigit(src: Point, dest: Point) = g.drawImage(
            digits, dest.x, dest.y, dest.x+size.x, dest.y+size.y,
            src.x, src.y, src.x+size.x, src.y+size.y, null)

        fun srcForDigit(d: Int) = Point(20 + 28*(d/5), 6 + 12*(d%5))

        // draw background and digits
        g.drawImage(bg, 0, 0, null)
        val first = Point(92, 24)
        drawDigit(srcForDigit(ep/10), first)
        drawDigit(srcForDigit(ep%10), Point(first.x, first.y+size.y))
        g.dispose()

        // upscale the image
        val scaled = BufferedImage(sf*bg.width, sf*bg.height, BufferedImage.TYPE_INT_RGB)
        AffineTransformOp(
            AffineTransform.getScaleInstance(sf.toDouble(), sf.toDouble()),
            AffineTransformOp.TYPE_NEAREST_NEIGHBOR
        ).filter(merged, scaled)

        // save final image
        val out = thumbnailFile.get().asFile
        ImageIO.write(scaled, "PNG", out)

        logger.lifecycle("Saved thumbnail to {}", out.path)
    }
}


/**
 * Task to clean up any build artifacts
 */
tasks.register("clean") {
    doLast {
        delete(project.layout.buildDirectory)
    }
}
