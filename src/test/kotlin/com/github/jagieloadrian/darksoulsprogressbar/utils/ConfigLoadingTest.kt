package com.github.jagieloadrian.darksoulsprogressbar.utils

import com.github.jagieloadrian.darksoulsprogressbar.model.Icons
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class ConfigLoadingTest {

    @Test
    fun `icons yaml loads all entries`() {
        Icons.entries.size shouldBe 16
        Icons.entries.first().iconName shouldBe "Bonfire"
        Icons.entries.first().path shouldBe "/gif/bonfire_darksouls.gif"
    }

    @Test
    fun `items yaml loads all constants`() {
        Items.YOU_DIED_GIF shouldBe "/gif/you_died.gif"
        Items.ELDEN_RING_SOUNDTRACK shouldNotBe null
    }

    @Test
    fun `names yaml loads all constants`() {
        Names.SETTINGS_NAME shouldBe "DarkSouls progress bar"
        Names.DS_PERSISTENT_TOPIC_NAME shouldBe "DSPersistentStateChanged"
    }
}
