package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeParam;
import de.pavloff.pycharm.plugin.macros.PyVariableMacro;
import de.pavloff.pycharm.plugin.server_stub.ServerStub;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Map;

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
        recomCodePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel mainPanel = new JPanel();
        searchField = new JBTextField();

        JBScrollPane recomCodePanelWrapper = new JBScrollPane(
                recomCodePanel, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(recomCodePanelWrapper)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(searchField, 200, 400, 600)
                                                .addGap(100)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(recomCodePanelWrapper, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        return mainPanel;
    }

    private void handleDocumentEvent(DocumentEvent e) {
        // TODO: implement a delay for input
        ServerStub serverStub = ServerStub.getInstance(openedProject);
        Document doc = e.getDocument();
        try {
            // read always full text
            String input = doc.getText(0, doc.getLength());
            serverStub.onInput(input);
            updateAndDisplayRecommendations();
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

    }

    /** This method should be called to update the list of recommendations.
     *  It replaces the usage of the CodeFragmentListener
     */
    public void updateAndDisplayRecommendations() {
        ServerStub serverStub = ServerStub.getInstance(openedProject);
        LinkedHashSet<CodeFragment> newRecommendations = serverStub.getRecommendations();
        EventQueue.invokeLater(() -> { repaintRecommendations(serverStub, newRecommendations); });
    }

    /** Code which repaints recommendations, and adds listener to each recommended fragement for mause click
     * @param serverStub current CodeFragmentManager
     * @param fragments set of current fragments
     */
    private void repaintRecommendations(ServerStub serverStub, LinkedHashSet<CodeFragment> fragments) {
        if (recomCodePanel == null) return;

        recomCodePanel.removeAll();

        if (fragments == null) {
            return;
        }

        for (CodeFragment fragment : fragments) {
            RecomCode r = new RecomCode(fragment);

            r.addListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    serverStub.onCodeFragment(fragment);

                    Editor editor = FileEditorManager.getInstance(openedProject).getSelectedTextEditor();

                    if (editor != null) {
                        TemplateManager templateManager = TemplateManagerImpl.getInstance(openedProject);
                        templateManager.startTemplate(editor, newTemplate(fragment));
                        IdeFocusManager.getInstance(openedProject).requestFocus(editor.getContentComponent(), true);
                    }
                }
            });

            recomCodePanel.add(r);
        }
        recomCodePanel.repaint();
    }

    private Template newTemplate(CodeFragment fragment) {
        TemplateManager templateManager = TemplateManagerImpl.getInstance(openedProject);
        Template t = templateManager.createTemplate(fragment.getRecID(), fragment.getGroup(), fragment.getCode());
        t.setToReformat(false);
        t.setToIndent(false);

        Map<String, CodeParam> params = fragment.getDefaultParams();
        String[] variables = fragment.getParamsList();

        for (String v : variables) {
            CodeParam p = null;

            if (params.containsKey(v)) {
                p = params.get(v);
            }

            if (p != null) {
                if (p.hasExpression()) {
                    t.addVariable(p.getName(), p.getExpr(), p.getVars(), true);
                } else {
                    String[] vars = p.getVars().split("\\|");

                    if (vars.length > 1) {
                        MacroCallNode macro = new MacroCallNode(new PyVariableMacro(vars));
                        t.addVariable(p.getName(), macro, true);

                    } else {
                        if (vars[0].length() == 0) {
                            t.addVariable(p.getName(), new TextExpression(vars[0]), true);
                        } else {
                            // just put default value and continue
                            t.addVariable(p.getName(), new TextExpression(vars[0]), false);
                        }
                    }
                }
            }
        }

        return t;
    }
}
