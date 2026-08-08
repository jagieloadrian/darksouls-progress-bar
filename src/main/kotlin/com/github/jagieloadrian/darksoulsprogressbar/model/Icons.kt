package com.github.jagieloadrian.darksoulsprogressbar.model

import com.github.jagieloadrian.darksoulsprogressbar.utils.loadYamlResource

data class Icon(val iconName: String, val path: String)

object Icons {
    @Suppress("UNCHECKED_CAST")
    val entries: List<Icon> =
        (loadYamlResource("/config/icons.yaml")["icons"] as List<Map<String, String>>)
            .map { Icon(it.getValue("name"), it.getValue("path")) }
}
