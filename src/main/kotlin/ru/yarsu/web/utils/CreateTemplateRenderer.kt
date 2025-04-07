package ru.yarsu.web.utils

import org.http4k.template.PebbleTemplates
import org.http4k.template.TemplateRenderer
import ru.yarsu.config.WebConfig

const val TEMPLATES_DIR = "src/main/resources"

fun createTemplateRenderer(webConfig: WebConfig): TemplateRenderer =
    PebbleTemplates().let { templates ->
        if (webConfig.hotReloadTemplates) {
            templates.HotReload(TEMPLATES_DIR)
        } else {
            templates.CachingClasspath()
        }
    }
