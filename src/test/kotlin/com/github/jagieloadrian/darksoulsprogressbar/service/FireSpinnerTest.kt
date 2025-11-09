package com.github.jagieloadrian.darksoulsprogressbar.service

import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class FireSpinnerTest {

    @Test
    fun `should return image icon`() {
        //given
        val spinner = FireSpinner()

        //when
        val actual = spinner.getIcon()

        //then
        actual shouldNotBe null
        actual.description shouldContain "gif"
    }
}