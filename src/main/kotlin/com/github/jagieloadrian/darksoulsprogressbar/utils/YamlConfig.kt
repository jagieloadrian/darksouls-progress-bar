package com.github.jagieloadrian.darksoulsprogressbar.utils

import org.yaml.snakeyaml.Yaml

@Suppress("UNCHECKED_CAST")
fun loadYamlResource(path: String): Map<String, Any> {
    val stream = object {}.javaClass.getResourceAsStream(path)
        ?: error("Missing config resource: $path")
    return Yaml().load(stream) as Map<String, Any>
}
