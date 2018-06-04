package de.pavloff.recomcode.plugin.recommender;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextField;
import de.pavloff.recomcode.core.CodeFragment;
import de.pavloff.recomcode.core.CodeFragmentManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
        recomCodePanel.setPreferredSize(new Dimension(0, 300));

        GroupLayout recomCodePanelLayout = new GroupLayout(recomCodePanel);
        recomCodePanel.setLayout(recomCodePanelLayout);
        recomCodePanelLayout.setHorizontalGroup(
                recomCodePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        recomCodePanelLayout.setVerticalGroup(
                recomCodePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 243, Short.MAX_VALUE)
        );


        JPanel mainPanel = new JPanel();
        searchField = new JBTextField();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(recomCodePanel, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 494, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 282, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(recomCodePanel, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
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
        recommender.handleDocumentEvent(e);
    }

    private void setCodeFragmentHandler() {
        CodeFragmentManager recommender = CodeFragmentManager.getInstance(openedProject);
        recommender.addCodeFragmentListener(fragments -> {
            for (CodeFragment fragment : fragments) {
                recomCodePanel.add(new RecomCode(fragment));
            }
        });
    }
}
