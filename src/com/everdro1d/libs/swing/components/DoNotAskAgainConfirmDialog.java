/**************************************************************************************************
 * Copyright (c) dro1dDev 2024.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class DoNotAskAgainConfirmDialog extends JPanel {
    private JCheckBox doNotAskAgainCheckBox;

    private DoNotAskAgainConfirmDialog(Object message) {
        setLayout(new BorderLayout());

        if (message instanceof Component) {
            add((Component) message);
        } else if (message != null) {
            JLabel messageLabel = new JLabel("<html>" + message + "</html>");
            messageLabel.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
            add(messageLabel);
        }

        doNotAskAgainCheckBox = new JCheckBox("Don't ask me again");
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.add(doNotAskAgainCheckBox);
        add(checkBoxPanel, BorderLayout.SOUTH);
    }

    public boolean isDoNotAskAgainSelected() { return doNotAskAgainCheckBox.isSelected(); }

    /**
     * Shows a confirmation dialog with the selected options.
     * @param parentComponent Parent
     * @param message Message to show in the dialog <html> format
     * @param title Dialog title
     * @param optionType JOptionPane.[OPTIONS]
     * @param messageType JOptionPane.[MESSAGE_TYPE]
     * @param prefs user preferences to save do not ask again
     * @param prefsKey key for do not ask again
     * @return int selected option
     */
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Preferences prefs, String prefsKey) {
        int result;

        if (prefs.getBoolean(prefsKey, false)) {
            return JOptionPane.YES_OPTION;
        } else {
            DoNotAskAgainConfirmDialog confirmDialog = new DoNotAskAgainConfirmDialog(message);
            result = JOptionPane.showOptionDialog(parentComponent, confirmDialog, title, optionType, messageType, null, null, null);
            if (confirmDialog.isDoNotAskAgainSelected()) {
                prefs.putBoolean(prefsKey, true);
            }
        }
        return result;
    }
}
