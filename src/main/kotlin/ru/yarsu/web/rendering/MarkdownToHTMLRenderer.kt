package ru.yarsu.web.rendering

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet

object MarkdownToHTMLRenderer {
    // Set options to support different Markdown notations
    private val options = MutableDataSet().also {
        it[Parser.EXTENSIONS] = listOf(
            StrikethroughExtension.create(), // Corresponds to strikethrough
            TablesExtension.create(), // Compatible with tables
            TocExtension.create(), // [TOC]Generate a table of contents in the part of
        )
        it[HtmlRenderer.ESCAPE_HTML] = true
        it[HtmlRenderer.ESCAPE_INLINE_HTML] = true
        it[HtmlRenderer.ESCAPE_HTML_BLOCKS] = true
        it[HtmlRenderer.ESCAPE_INLINE_HTML_COMMENTS] = true
        it[HtmlRenderer.ESCAPE_HTML_COMMENT_BLOCKS] = true
    }

    private val parser = Parser.builder(options).build()
    private val renderer = HtmlRenderer.builder(options).build()

    fun render(markdown: String): String = renderer.render(parser.parse(markdown))
}
