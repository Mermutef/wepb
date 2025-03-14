package ru.yarsu.web.context.templates

import ru.yarsu.config.WebConfig

const val TEMPLATES_DIR = "src/main/resources"

fun createContextAwareTemplateRenderer(webConfig: WebConfig): ContextAwareTemplateRenderer =
    ContextAwarePebbleTemplates().let { templates ->
        if (webConfig.hotReloadTemplates) {
            templates.HotReload(TEMPLATES_DIR)
        } else {
            templates.CachingClasspath()
        }
    }
