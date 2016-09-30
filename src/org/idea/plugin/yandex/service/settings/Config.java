package org.idea.plugin.yandex.service.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.idea.plugin.yandex.service.state.State;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ivan on 29.09.2016.
 */
public class Config implements Configurable {
    final State state = ServiceManager.getService(State.class);

    JPanel panel = new JPanel();
    JTextField tfApikey = new JTextField(state.translateApiKey);
    JTextField tfTrslDir = new JTextField(state.translateDirection);


    public String getDisplayName(){
        return "Yandex services";
    }

    public String getHelpTopic() {
        return null;
    }

    public boolean isModified() {
        return state.translateApiKey != tfApikey.getText() || state.translateDirection != tfTrslDir.getText();
    }

    public void disposeUIResources() {
        panel = null;
    }

    public void apply() {
        state.translateApiKey = tfApikey.getText();
        state.translateDirection = tfTrslDir.getText();
    }

    public void reset() {
        tfApikey.setText(state.translateApiKey);
        tfTrslDir.setText(state.translateDirection);
    }

    public JComponent createComponent() {
        panel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(new JLabel("Yandex Translate"), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(new JLabel("API key"), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(tfApikey, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(new JLabel("Translation direction"), new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(tfTrslDir, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(new Spacer(), new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        return panel;
    }
}
