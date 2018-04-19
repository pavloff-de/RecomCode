package de.pavloff.recomcode.core.plugin.recommender;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class RecomCodeManager {

    private Project openedProject;

    private JTextField searchField;
    private JPanel recomCodePanel;

    public static RecomCodeManager getInstance(Project project) {
        return project.getComponent(RecomCodeManager.class);
    }

    JComponent initView(Project project) {
        openedProject = project;

        JPanel mainPanel = new JPanel();
        searchField = new JTextField();
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

        return mainPanel;
    }
}
