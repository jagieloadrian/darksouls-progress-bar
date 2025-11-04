package com.github.jagieloadrian.darksoulsprogressbar.settings

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

}