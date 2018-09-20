package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import de.pavloff.pycharm.core.CodeFragment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

class RecomCode extends JPanel {

    private Dimension mainSize = new Dimension(250, 100);
    private Dimension textSize = new Dimension(240, 70);

    private JLabel fragmentName;

    RecomCode(CodeFragment fragment) {
        setPreferredSize(mainSize);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        fragmentName = new JLabel();
        fragmentName.setMinimumSize(textSize);
        fragmentName.setPreferredSize(textSize);
        fragmentName.setMaximumSize(textSize);

        String[] textkeys = fragment.getCleanTextkeys();
        String textkey = fragment.getCode(); // no text ?

        if (textkeys != null && textkeys.length != 0) {
            textkey = textkeys[0];
        }
        fragmentName.setText("<html>" + textkey + "</html>");
        add(fragmentName, c);

        c.gridx = 0;
        c.gridy = 1;
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
        g.setColor(Color.BLACK);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // i dont know which
    }
}
