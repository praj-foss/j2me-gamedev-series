import com.github.gradle.node.npm.task.NpxTask
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download.set(true)
    version.set("18.17.1")
}

/**
 * Task to start slideshow using Marp CLI.
 * Use it like:
 *    ./gradlew :slideshow
 *
 * It works just like the following command:
 *    npx @marp-team/marp-cli@4.1.2 -s .
 */
tasks.register<NpxTask>("slideshow") {
    command.set("@marp-team/marp-cli@4.1.2")
    args.set(listOf("-s", "."))
//    environment.set(mapOf("PORT" to "9090")) // for a custom port
}

data class Point(
    val x: Int, val y: Int
)
val episode = providers.gradleProperty("episode")
val thumbnailFile = project.layout.buildDirectory.file("thumbnail.png")

/**
 * Task to create thumbnail for YouTube videos.
 * Use it like:
 *    ./gradlew :thumbnail -Pepisode=42
 */
tasks.register("thumbnail") {
    outputs.file(thumbnailFile)

    val ep = try {
        if (!episode.isPresent) throw IllegalStateException()
        val parsed = episode.get().toInt()
        if (parsed !in 0..99) throw Exception()
        parsed
    } catch (_: IllegalStateException) {
        throw GradleException("This task needs property 'episode' to be set. " +
                "It can be specified using CLI. For example: -Pepisode=69")
    } catch (_: Exception) {
        throw GradleException("Property 'episode' must be a number within 0 to 99")
    }

    fun start(digit: Int) = Point(20 + 28*(digit/5), 6 + 12*(digit%5))
    fun end(p: Point) = Point(p.x + 16, p.y + 12)

    doLast {
        logger.lifecycle("Generating thumbnail for episode {}", ep)

        // read source images
        val bg = ImageIO.read(project.file("2025/thumbnails/bg.png"))
        val digits = ImageIO.read(project.file("2025/thumbnails/digits.png"))

        val start = Point(92, 24)       // starting point of first digit
        val size = Point(16, 12)        // width and height for each digit

        val merged = BufferedImage(bg.width, bg.height, BufferedImage.TYPE_INT_RGB)
        val g = merged.createGraphics()
        g.drawImage(bg, 0, 0, null)

        // draw first digit
        val s1 = start(ep/10)
        val e1 = end(s1)
        g.drawImage(digits, start.x, start.y, start.x+size.x, start.y + size.y,
            s1.x, s1.y, e1.x, e1.y, null)

        // draw first digit
        val s2 = start(ep%10)
        val e2 = end(s2)
        g.drawImage(digits, start.x, start.y+size.y, start.x+size.x, start.y + 2*size.y,
            s2.x, s2.y, e2.x, e2.y, null)
        g.dispose()

        // scale the image by 10x
        val factor = 10
        val scaled = BufferedImage(factor*bg.width, factor*bg.height, BufferedImage.TYPE_INT_RGB)
        AffineTransformOp(
            AffineTransform.getScaleInstance(factor.toDouble(), factor.toDouble()),
            AffineTransformOp.TYPE_NEAREST_NEIGHBOR
        ).filter(merged, scaled)

        // save final image
        val out = thumbnailFile.get().asFile
        ImageIO.write(scaled, "PNG", out)

        logger.lifecycle("Saved thumbnail to {}", out.path)
    }
}
