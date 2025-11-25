package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.model.Icons
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class DSPersistentStateTest {

    @Test
    fun `given a class instance when getState should not be null`() {
        //given
        val persistentState = DSPersistentState()

        //when
        val actual = persistentState.state

        //then
        actual shouldNotBe null
    }

    @Test
    fun `iconPaths should be initialized from Icons entries`() {
        val state = DSPersistentState()

        //
        state.iconPaths shouldContain Icons.entries.random().path
    }

    @Test
    fun `animateOnFailedBuild should default to true`() {
        val state = DSPersistentState()
        state.animateOnFailedBuild shouldBe true
    }

    @Test
    fun `loadState should copy values from another state`() {
        val original = DSPersistentState().apply {
            iconPaths.clear()
            iconPaths.add("customPath")
            animateOnFailedBuild = false
        }

        val target = DSPersistentState()
        target.loadState(original)

        target.iconPaths shouldContain "customPath"
        target.animateOnFailedBuild shouldBe false
    }

}