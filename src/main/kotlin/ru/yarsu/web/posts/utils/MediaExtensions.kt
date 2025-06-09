package ru.yarsu.web.posts.utils

import ru.yarsu.domain.models.MediaType
import ru.yarsu.web.rendering.MarkdownToHTMLRenderer
import kotlin.collections.get
import kotlin.text.get

fun MediaType.html(filename: String): String =
    when (this) {
        MediaType.VIDEO ->
            """
            <div class="row my-3">
                <div class="col-sm-1 col-lg-2"></div>
                <div class="col-sm-10 col-lg-8">
                    <iframe style="aspect-ratio: 16/9; width: 100%;"
                            src="$filename" title="External video player"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media;
                            gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"
                            allowfullscreen>
                    </iframe>
                </div>
            </div>
            """.trimIndent()

        MediaType.IMAGE ->
            """
            <div class="row my-3">
                <div class="col-sm-1 col-lg-2"></div>
                <div class="col-sm-10 col-lg-8">
                    <img class="img-thumbnail" src="/media/$filename"/>
                </div>
            </div>
            """.trimIndent()

        MediaType.SOUND ->
            """
            <div class="row my-3">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                    <audio class="my-3 w-100" controls><source src="/media/$filename" type="audio/mpeg"></audio>
                </div>
            </div>
            """.trimIndent()
    }

private fun String.replaceQuotes() = this.replace("<", "{").replace(">", "}")

fun String.render(): String {
    val media = MediaType.entries.associateWith { mediaType -> mediaType.pattern.findAll(this).toList() }
    var rawString = this

    media.keys.forEach { mediaType ->
        media[mediaType]!!.forEach { filename ->
            rawString = rawString.replace(
                filename.groupValues[0],
                filename.groupValues[0].replaceQuotes()
            )
        }
    }
    rawString = MarkdownToHTMLRenderer.render(rawString)

    media.keys.forEach { mediaType ->
        media[mediaType]!!.forEach { filename ->
            rawString = rawString.replace(
                filename.groupValues[0].replaceQuotes(),
                mediaType.html(filename.groupValues[1])
            )
        }
    }

    return rawString
}
