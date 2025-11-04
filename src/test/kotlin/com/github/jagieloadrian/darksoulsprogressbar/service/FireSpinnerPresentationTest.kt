package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.CUSTOM_WIDGET_NAME
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.swing.ImageIcon

@ExtendWith(MockKExtension::class)
class FireSpinnerPresentationTest {

    @RelaxedMockK
    lateinit var spinner: FireSpinner

    @InjectMockKs
    lateinit var presenter: FireSpinnerPresentation

    @Test
    fun `should return expected tooltip text`() {
        //when
        val actual = presenter.getTooltipText()

        //then
        actual shouldBe CUSTOM_WIDGET_NAME
    }

    @Test
    fun `should return expected image icon`() {
        //given
        val mockIcon = mockk<ImageIcon>()
        every { spinner.getIcon() } returns mockIcon

        //when
        val actual = presenter.getIcon()

        //then
        actual shouldBe mockIcon
    }

    @Test
    fun `should return action after click consumer`() {
        //given

        //when
        val actual = presenter.getClickConsumer()

        //then
        actual shouldNotBe null
    }

}