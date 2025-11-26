package com.github.jagieloadrian.darksoulsprogressbar.listeners;

import com.github.jagieloadrian.darksoulsprogressbar.ui.DSProgressBarFactory;
import com.intellij.ide.ui.LafManager;
import com.intellij.openapi.wm.IdeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.UIManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DSProgressBarComponentTest {

    private DSProgressBarComponent component;

    @BeforeEach
    void setUp() {
        component = new DSProgressBarComponent();
        // clean ui manager
        UIManager.getDefaults().remove("ProgressBarUI");
        UIManager.getDefaults().remove(DSProgressBarFactory.class.getName());
    }

    @Test
    void lookAndFeelChanged_shouldCallUpdateProgressBarUI() {
        // given
        LafManager lafManager = mock(LafManager.class);

        // when
        component.lookAndFeelChanged(lafManager);

        // then
        verifyUIManagerWasUpdatedCorrectly();
    }

    @Test
    void applicationActivated_shouldCallUpdateProgressBarUI() {
        // given
        IdeFrame ideFrame = mock(IdeFrame.class);

        // when
        component.applicationActivated(ideFrame);

        // then
        verifyUIManagerWasUpdatedCorrectly();
    }

    @Test
    void updateProgressBarUI_staticMethod_shouldPutCorrectValuesIntoUIManager() {
        // when
        DSProgressBarComponent.updateProgressBarUI();

        // then
        verifyUIManagerWasUpdatedCorrectly();
    }

    @Test
    void updateProgressBarUI_shouldBeIdempotent_whenCalledMultipleTimes() {
        // when
        DSProgressBarComponent.updateProgressBarUI();
        DSProgressBarComponent.updateProgressBarUI();
        DSProgressBarComponent.updateProgressBarUI();

        // then
        verifyUIManagerWasUpdatedCorrectly();

        assertEquals(
                DSProgressBarFactory.class.getName(),
                UIManager.get("ProgressBarUI")
        );
        assertEquals(
                DSProgressBarFactory.class,
                UIManager.getDefaults().get(DSProgressBarFactory.class.getName())
        );
    }

    private void verifyUIManagerWasUpdatedCorrectly() {
        String expectedUiKey = DSProgressBarFactory.class.getName(); // DSProgressBarFactory.class.getName()

        assertEquals(expectedUiKey, UIManager.get("ProgressBarUI"),
                "UIManager should have ProgressBarUI mapped to DSProgressBarFactory");

        assertSame(DSProgressBarFactory.class,
                UIManager.getDefaults().get(expectedUiKey),
                "UIDefaults should contain DSProgressBarFactory class under its full name");
    }
}