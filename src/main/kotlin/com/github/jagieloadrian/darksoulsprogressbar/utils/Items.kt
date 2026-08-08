package com.github.jagieloadrian.darksoulsprogressbar.utils

@Suppress("UNCHECKED_CAST")
object Items {
    private val config = loadYamlResource("/config/items.yaml")
    private val gifs = config["gifs"] as Map<String, String>
    private val sound = config["sound"] as Map<String, String>
    private val soundtrack = config["soundtrack"] as Map<String, String>

    val YOU_DIED_GIF: String = gifs.getValue("youDied")
    val SMALL_BONFIRE_GIF: String = gifs.getValue("smallBonfire")
    val BACKGROUND_PROGRESS_BAR_GIF: String = gifs.getValue("backgroundProgressBar")

    val YOU_DIED_SOUND: String = sound.getValue("youDied")

    val DS_ONE_SOUNDTRACK: String = soundtrack.getValue("dsOne")
    val DS_TWO_SOUNDTRACK: String = soundtrack.getValue("dsTwo")
    val DS_THREE_SOUNDTRACK: String = soundtrack.getValue("dsThree")
    val ELDEN_RING_SOUNDTRACK: String = soundtrack.getValue("eldenRing")
}
