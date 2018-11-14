package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import de.pavloff.pycharm.core.CodeFragment;
import de.pavloff.pycharm.core.CodeParam;
import de.pavloff.pycharm.plugin.macros.PyVariableMacro;
import de.pavloff.pycharm.plugin.serverstub.ServerStub;
import com.intellij.openapi.diagnostic.Logger;      // output in In ${idea.system.path}/log/idea.log
import de.pavloff.pycharm.plugin.serverstub.ServerStubFactory;

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

/** Implements the plugin component which manages the recommendations, and contains the
 * main "event handling" for recommendations.
 * Implements the ProjectComponent interface, see https://goo.gl/kjpXga
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under
 * <project-components>.
 */

public class RecomCodeManager implements ProjectComponent {

    private JPanel toolWindow;

    private JPanel recomCodePanel;

    private Project openedProject;

    private static Logger logger = Logger.getInstance(RecomCodeManager.class);

    public RecomCodeManager(Project project) {
        openedProject = project;

        createRecommenderPanel();

        ServerStub server = ServerStubFactory.getInstance();
        server.initialize(openedProject);
    }

    public static RecomCodeManager getInstance(Project project) {
        return project.getComponent(RecomCodeManager.class);
    }

    /**
     * returns the GUI for ToolWindow
     */
    Component getToolWindowComponent() {
        return toolWindow;
    }


    /**
     * creates the GUI for ToolWindow
     *
     * recommender panel contains
     * text field for user inputs
     * pane to display the recommendations
     */
    private void createRecommenderPanel() {
        logger.debug("creating Recommender Panel..");

        JBTextField searchField = new JBTextField();
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

        recomCodePanel = new JPanel();
        recomCodePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JBScrollPane recomCodePanelWrapper = new JBScrollPane(
                recomCodePanel, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        toolWindow = new JPanel();
        GroupLayout layout = new GroupLayout(toolWindow);
        toolWindow.setLayout(layout);
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

        logger.debug("Recommender Panel created");
    }

    /**
     * handles the events of user input
     */
    private void handleDocumentEvent(DocumentEvent e) {
        // TODO: implement a delay for input
        Document doc = e.getDocument();
        String input;
        try {
            // read always full text
            input = doc.getText(0, doc.getLength());
        } catch (BadLocationException err) {
            logger.debug(String.format("error occurred while reading user input: '%s'",
                    err.getMessage()));
            return;
        }

        ServerStub serverStub = ServerStubFactory.getInstance();
        serverStub.onInput(input);
        updateAndDisplayRecommendations();

    }

    /** This method should be called to update the list of recommendations.
     *  It replaces the usage of the CodeFragmentListener
     */
    public void updateAndDisplayRecommendations() {
        logger.debug("updating recommendations..");
        ServerStub serverStub = ServerStubFactory.getInstance();
        LinkedHashSet<CodeFragment> newRecommendations = serverStub.getRecommendations();
        EventQueue.invokeLater(() -> repaintRecommendations(serverStub, newRecommendations));
    }

    /** Code which repaints recommendations, and adds listener to each recommended fragement for mause click
     * @param serverStub current CodeFragmentManager
     * @param fragments set of current fragments
     */
    private void repaintRecommendations(ServerStub serverStub, LinkedHashSet<CodeFragment> fragments) {
        logger.debug("repainting recommendations..");
        if (recomCodePanel == null) {
            logger.debug("..recommender panel is not initialized");
            return;
        }

        recomCodePanel.removeAll();

        if (fragments == null) {
            return;
        }

        logger.debug("creating recommendations..");
        for (CodeFragment fragment : fragments) {
            RecomBox r = new RecomBox(fragment);

            r.addListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    logger.debug(String.format("template for fragment '%s' selected",
                            fragment.getRecID()));
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
        logger.debug(String.format("creating template for fragment '%s'..", fragment.getRecID()));

        TemplateManager templateManager = TemplateManagerImpl.getInstance(openedProject);
        Template t = templateManager.createTemplate(fragment.getRecID(), fragment.getGroup(), fragment.getCode());
        t.setToReformat(false);
        t.setToIndent(false);

        Map<String, CodeParam> params = fragment.getDefaultParams();
        String[] variables = fragment.getParamsList();

        logger.debug("searching for relevant variables..");
        for (String v : variables) {
            CodeParam p = null;

            if (params.containsKey(v)) {
                p = params.get(v);
            }

            if (p != null) {
                if (p.hasExpression()) {
                    logger.debug("..variable with expression");
                    t.addVariable(p.getName(), p.getExpr(), p.getVars(), true);
                } else {
                    String[] vars = p.getVars().split("\\|");

                    if (vars.length > 1) {
                        logger.debug("..variable with examples");
                        MacroCallNode macro = new MacroCallNode(new PyVariableMacro(vars));
                        t.addVariable(p.getName(), macro, true);

                    } else {
                        if (vars[0].length() == 0) {
                            logger.debug("..empty variable");
                            t.addVariable(p.getName(), new TextExpression(vars[0]), true);
                        } else {
                            // just put default value and continue
                            logger.debug("..initialized variable");
                            t.addVariable(p.getName(), new TextExpression(vars[0]), false);
                        }
                    }
                }
            }
        }

        return t;
    }

    @Override
    public void projectOpened() {
        // TODO: ???
    }

    @Override
    public void projectClosed() {
        // TODO: ???
    }

    @Override
    public void initComponent() {
        // TODO: ???
    }

    @Override
    public void disposeComponent() {
        // TODO: ???
    }
}
