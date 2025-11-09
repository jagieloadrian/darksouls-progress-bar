package com.github.jagieloadrian.darksoulsprogressbar.listeners;

import com.github.jagieloadrian.darksoulsprogressbar.ui.DSProgressBarFactory;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.UIManager;

public class DSProgressBarComponent implements LafManagerListener, ApplicationActivationListener {
    private static final String PROGRESS_BAR_UI_KEY = "ProgressBarUI";
    private static final String DS_PROGRESS_BAR_UI_IMPLEMENTATION_NAME = DSProgressBarFactory.class.getName();

    @Override
    public void lookAndFeelChanged(@NotNull LafManager lafManager) {
        updateProgressBarUI();
    }

    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        updateProgressBarUI();
    }

    static void updateProgressBarUI() {
        UIManager.put(PROGRESS_BAR_UI_KEY, DS_PROGRESS_BAR_UI_IMPLEMENTATION_NAME);
        UIManager.getDefaults().put(DS_PROGRESS_BAR_UI_IMPLEMENTATION_NAME, DSProgressBarFactory.class);
    }
}
