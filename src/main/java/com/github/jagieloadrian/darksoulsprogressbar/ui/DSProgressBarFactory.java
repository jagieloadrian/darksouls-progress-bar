package com.github.jagieloadrian.darksoulsprogressbar.ui;

import com.intellij.util.ui.JBUI;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class DSProgressBarFactory extends BasicProgressBarUI {
    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
        return new DSProgressBarUI();
    }
}
