package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.CUSTOM_WIDGET_NAME
import com.intellij.openapi.project.Project
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DSSpinnerStatusBarFactoryTest {

    lateinit var widgetBarFactory: DSSpinnerStatusBarFactory

    @BeforeEach
    fun setUp() {
        widgetBarFactory = DSSpinnerStatusBarFactory()
    }

    @Test
    fun `should return expected id`(){
        //when
        val actual = widgetBarFactory.id

        //then
        actual shouldBe CUSTOM_WIDGET_NAME
    }

    @Test
    fun `should return expected display name`(){
        //when
        val actual = widgetBarFactory.displayName

        //then
        actual shouldBe CUSTOM_WIDGET_NAME
    }

    @Test
    fun `should return new FireSpinnerWidget`(){
        //given
        val mockProject = mockk<Project>()
        val mockScope = mockk<CoroutineScope>()

        //when
        val actual = widgetBarFactory.createWidget(mockProject, mockScope)

        //then
        actual shouldNotBe null
        actual.shouldBeInstanceOf<FireSpinnerWidget>()
    }

    @Test
    fun `should return true when isEnabledByDefault`(){
        //when
        val actual = widgetBarFactory.isEnabledByDefault

        //then
        actual shouldBe true
    }

    @Test
    fun `should return true when isAvailable`(){
        //given
        val mockProject = mockk<Project>()

        //when
        val actual = widgetBarFactory.isAvailable(mockProject)

        //then
        actual shouldBe true
    }
}