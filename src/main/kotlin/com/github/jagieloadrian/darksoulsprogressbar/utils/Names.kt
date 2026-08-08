package com.github.jagieloadrian.darksoulsprogressbar.utils

object Names {
    private val config = loadYamlResource("/config/names.yaml")

    val CUSTOM_WIDGET_NAME: String = config.getValue("customWidgetName") as String
    val SETTINGS_NAME: String = config.getValue("settingsName") as String
    val DS_PERSISTENT_TOPIC_NAME: String = config.getValue("dsPersistentTopicName") as String
    val TEST_FAILURE_WINDOW_DESC: String = config.getValue("testFailureWindowDesc") as String
    val TEST_FAILURE_WINDOW_NAME: String = config.getValue("testFailureWindowName") as String
    val DS_PLUGIN_CONFIGURABLE_NAME: String = config.getValue("dsPluginConfigurableName") as String
}
