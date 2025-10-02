package com.github.jagieloadrian.darksoulsprogressbar.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class DSProgressBarFactory extends BasicProgressBarUI {
    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new DSProgressBarUI();
    }
}
