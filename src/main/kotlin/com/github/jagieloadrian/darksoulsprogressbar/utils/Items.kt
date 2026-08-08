package com.github.jagieloadrian.darksoulsprogressbar.utils

object Items {
    private val config = loadYamlResource("/config/items.yaml")

    val YOU_DIED_GIF: String = config["youDiedGif"] as String
    val YOU_DIED_SOUND: String = config["youDiedSound"] as String
    val SMALL_BONFIRE_GIF: String = config["smallBonfireGif"] as String
    val BACKGROUND_PROGRESS_BAR_GIF: String = config["backgroundProgressBarGif"] as String
    val DS_ONE_SOUNDTRACK: String = config["dsOneSoundtrack"] as String
    val DS_TWO_SOUNDTRACK: String = config["dsTwoSoundtrack"] as String
    val DS_THREE_SOUNDTRACK: String = config["dsThreeSoundtrack"] as String
    val ELDEN_RING_SOUNDTRACK: String = config["eldenRingSoundtrack"] as String
}
