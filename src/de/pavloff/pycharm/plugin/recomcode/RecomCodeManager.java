package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextField;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeFragmentManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

public class RecomCodeManager {

    private JBTextField searchField;
    private JPanel recomCodePanel;
    private Project openedProject;

    public static RecomCodeManager getInstance(Project project) {
        return project.getComponent(RecomCodeManager.class);
    }

    Component initView(Project project) {
        openedProject = project;

        recomCodePanel = new JPanel();
        recomCodePanel.setPreferredSize(new Dimension(500, 300));
        recomCodePanel.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel();
        searchField = new JBTextField();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(recomCodePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                                .addGap(76, 76, 76)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(recomCodePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleDocumentEvent(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleDocumentEvent(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleDocumentEvent(e);
            }
        });

        setCodeFragmentHandler();

        return mainPanel;
    }

    private void handleDocumentEvent(DocumentEvent e) {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        Document doc = e.getDocument();
        try {
            // read always full text
            String input = doc.getText(0, doc.getLength());
            recommender.onInput(input);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

    }

    private void setCodeFragmentHandler() {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.addCodeFragmentListener(fragments -> {
            recomCodePanel.removeAll();
            for (CodeFragment fragment : fragments) {
                recomCodePanel.add(new RecomCode(fragment));
            }
            recomCodePanel.repaint();
        });
    }
}
