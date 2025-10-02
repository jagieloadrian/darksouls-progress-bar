package com.github.jagieloadrian.darksoulsprogressbar.listeners;

import com.github.jagieloadrian.darksoulsprogressbar.ui.TestFailureOverlay;
import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.openapi.diagnostic.DefaultLogger;
import io.opentelemetry.api.logs.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class DSTestListenerComponent extends SMTRunnerEventsAdapter {
    private DefaultLogger logger = new DefaultLogger("INFO");

    @Override
    public void onTestFailed(@NotNull SMTestProxy test) {
        logger.info("Wszedłem w failed test");
        SwingUtilities.invokeLater(TestFailureOverlay.INSTANCE::show);
        super.onTestFailed(test);
    }

    @Override
    public void onCustomProgressTestFailed() {
        logger.info("Wszedłem w custom failed test");
        SwingUtilities.invokeLater(TestFailureOverlay.INSTANCE::show);
        super.onCustomProgressTestFailed();
    }
}
