package com.github.jagieloadrian.darksoulsprogressbar.utils

object Names {
    private val config = loadYamlResource("/config/names.yaml")

    val CUSTOM_WIDGET_NAME: String = config["customWidgetName"] as String
    val SETTINGS_NAME: String = config["settingsName"] as String
    val DS_PERSISTENT_TOPIC_NAME: String = config["dsPersistentTopicName"] as String
    val TEST_FAILURE_WINDOW_DESC: String = config["testFailureWindowDesc"] as String
}
