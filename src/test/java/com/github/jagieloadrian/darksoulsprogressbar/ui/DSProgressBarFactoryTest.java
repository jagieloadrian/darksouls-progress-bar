package com.github.jagieloadrian.darksoulsprogressbar.ui;

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DSProgressBarFactoryTest {

    private MockedStatic<ApplicationManager> applicationManagerMock;

    @BeforeEach
    void setup() {
        System.setProperty("java.awt.headless", "true");
        // Mock static ApplicationManager.getApplication()
        applicationManagerMock = mockStatic(ApplicationManager.class);

        Application mockApplication = mock(Application.class);
        DSPersistentState mockState = mock(DSPersistentState.class);

        // ApplicationManager.getApplication() -> mockApplication
        applicationManagerMock.when(ApplicationManager::getApplication)
                .thenReturn(mockApplication);

        // mockApplication.getService(DSPersistentState.class) -> mockState
        when(mockApplication.getService(DSPersistentState.class))
                .thenReturn(mockState);

        //message bus
        MessageBus mockBus = mock(MessageBus.class);
        MessageBusConnection mockConnection = mock(MessageBusConnection.class);

        when(mockApplication.getMessageBus()).thenReturn(mockBus);
        when(mockBus.connect()).thenReturn(mockConnection);

        // optional setup
        when(mockState.getIconPaths()).thenReturn(Set.of("icon1"));
        when(mockState.getAnimateOnFailedBuild()).thenReturn(true);
    }

    @AfterEach
    void teardown() {
        applicationManagerMock.close();
    }

    @Test
    void givenFactory_whenCreateComponent_thenShouldReturnDSProgressBarUI() {
        //given
        JComponent component = mock(JComponent.class);

        //when
        ComponentUI actual = DSProgressBarFactory.createUI(component);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(DSProgressBarUI.class);
    }

    @Test
    void givenFactory_whenCreateComponentTwice_thenShouldReturnTwiceDSProgressBarUIAndTheyAreNotTheSame() {
        //given
        JComponent component = mock(JComponent.class);

        //when
        ComponentUI actual = DSProgressBarFactory.createUI(component);
        ComponentUI actual2 = DSProgressBarFactory.createUI(component);

        //then
        assertThat(actual).isNotSameAs(actual2);
        assertThat(actual).isInstanceOf(DSProgressBarUI.class);
        assertThat(actual2).isInstanceOf(DSProgressBarUI.class);
    }
}