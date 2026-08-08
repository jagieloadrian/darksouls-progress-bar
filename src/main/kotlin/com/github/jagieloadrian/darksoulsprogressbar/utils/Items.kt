package com.github.jagieloadrian.darksoulsprogressbar.utils

@Suppress("UNCHECKED_CAST")
object Items {
    private val config = loadYamlResource("/config/items.yaml")
    private val gify = config["gify"] as Map<String, String>
    private val sound = config["sound"] as Map<String, String>
    private val soundtrack = config["soundtrack"] as Map<String, String>

    val YOU_DIED_GIF: String = gify.getValue("youDiedGif")
    val SMALL_BONFIRE_GIF: String = gify.getValue("smallBonfireGif")
    val BACKGROUND_PROGRESS_BAR_GIF: String = gify.getValue("backgroundProgressBarGif")

    val YOU_DIED_SOUND: String = sound.getValue("youDiedSound")

    val DS_ONE_SOUNDTRACK: String = soundtrack.getValue("dsOneSoundtrack")
    val DS_TWO_SOUNDTRACK: String = soundtrack.getValue("dsTwoSoundtrack")
    val DS_THREE_SOUNDTRACK: String = soundtrack.getValue("dsThreeSoundtrack")
    val ELDEN_RING_SOUNDTRACK: String = soundtrack.getValue("eldenRingSoundtrack")
}
