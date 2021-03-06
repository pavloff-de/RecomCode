package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.JBColor;
import de.pavloff.pycharm.core.CodeFragment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

/** Encapsulates single code recommendation, corresponding to a rectangle in the recommendation panel.
 *  In particular, it draws the rectangle as a JPanel with a JLabel, and adds mouse listener to it
 */
class RecomBox extends JPanel {

    // Changes the sizes of recommendation "rectangles" (x-size, y-size)
    private Dimension mainSize = new Dimension(300, 140);
    private Dimension textSize = new Dimension(295, 138);

    private JLabel fragmentName;

    RecomBox(CodeFragment fragment) {
        setPreferredSize(mainSize);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        // Settings of Jlabel are here: https://docs.oracle.com/javase/7/docs/api/javax/swing/JLabel.html
        fragmentName = new JLabel();
        fragmentName.setMinimumSize(textSize);
        fragmentName.setPreferredSize(textSize);
        fragmentName.setMaximumSize(textSize);
        fragmentName.setHorizontalAlignment(SwingConstants.CENTER);
        fragmentName.setVerticalAlignment(SwingConstants.CENTER);

        String textkey = fragment.getCleanTextkey();

        if (textkey == null || textkey.length() == 0) {
            // no text ?
            textkey = fragment.getCode();
        }

        // fragmentName.setText("<html><font size=6><div style='text-align: center;font-size:2ex;'>" + textkey + "</font></div></html>");
        fragmentName.setText("<html><div style='text-align: center;font-size:x-large;'>" + textkey + "</div></html>");
        fragmentName.setForeground(new JBColor(JBColor.DARK_GRAY, JBColor.LIGHT_GRAY));
        add(fragmentName, c);

        c.gridx = 0;
        c.gridy = 1;
        // placeholder for actions on recommendations. this can be used for
        // e.g. preview of result after run
        // See docu at https://goo.gl/3KhgEq
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("RecomCode.Toolbar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("RecomCode.Toolbar.ID", actionGroup, false);
        add(actionToolbar.getComponent(), c);
    }

    void addListener(MouseListener l) {
        fragmentName.addMouseListener(l);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // g.setColor(new JBColor(JBColor.LIGHT_GRAY, JBColor.DARK_GRAY));
        g.setColor(new JBColor(JBColor.ORANGE, JBColor.DARK_GRAY));
        // Paints rectangle "background" with rounded corners
        // fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight), https://goo.gl/Wqswqs
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
    }
}
