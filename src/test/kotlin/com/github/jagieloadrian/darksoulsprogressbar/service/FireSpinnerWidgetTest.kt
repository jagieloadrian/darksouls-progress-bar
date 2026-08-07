package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.CUSTOM_WIDGET_NAME
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import javax.swing.ImageIcon

class FireSpinnerWidgetTest {

    private val widget = FireSpinnerWidget()

    @Test
    fun `should return expected ID`() {
        //when
        val actual = widget.ID()

        //then
        actual shouldBe CUSTOM_WIDGET_NAME
    }

    @Test
    fun `should return spinner presentation`() {
        //when
        val actual = widget.getPresentation()

        //then
        actual shouldNotBe null
        actual.shouldBeInstanceOf<FireSpinnerWidget>()
    }

    @Test
    fun `should return new instance of widget`() {
        //when
        val actual = widget.copy()

        //then
        actual shouldNotBeSameInstanceAs widget
    }


    @Test
    fun `should return expected tooltip text`() {
        //when
        val actual = widget.getTooltipText()

        //then
        actual shouldBe CUSTOM_WIDGET_NAME
    }

    @Test
    fun `should return image icon`() {
        //when
        val actual = widget.getIcon()

        //then
        actual.shouldBeInstanceOf<ImageIcon>()
        actual.description shouldContain "gif"
    }

    @Test
    fun `should return action after click consumer`() {
        //given

        //when
        val actual = widget.getClickConsumer()

        //then
        actual shouldNotBe null
    }


}