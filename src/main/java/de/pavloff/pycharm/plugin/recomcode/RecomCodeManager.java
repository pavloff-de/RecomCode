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
import de.pavloff.pycharm.core.CodeFragmentManager;
import de.pavloff.pycharm.core.CodeParam;
import de.pavloff.pycharm.plugin.macros.PyVariableMacro;
import com.intellij.openapi.diagnostic.Logger;      // output in In ${idea.system.path}/log/idea.log

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Implements the plugin component which manages the recommendations, and contains the
 * main "event handling" for recommendations.
 * Implements the ProjectComponent interface, see https://goo.gl/kjpXga
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under
 * <project-components>.
 */
public class RecomCodeManager implements ProjectComponent {

    private JPanel toolWindow;

    private JPanel recomCodePanel;

    private JBTextField searchField;


    private JPanel filterPanel = new JPanel();

    private Project openedProject;

    private static Logger logger = Logger.getInstance(RecomCodeManager.class);

    public RecomCodeManager(Project project) {
        openedProject = project;

        createRecommenderPanel();
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
     * <p>
     * recommender panel contains
     * text field for user inputs
     * pane to display the recommendations
     */
    private void createRecommenderPanel() {
        logger.debug("creating Recommender Panel..");

        searchField = new JBTextField();
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
        recomCodePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeRecomCodePanel();
            }
        });
        JBScrollPane recomCodePanelWrapper = new JBScrollPane(
                recomCodePanel, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        CodeFragmentManager fragmentManager = CodeFragmentManager.getInstance(openedProject);
        Map<String, Boolean> fragmentFilter = fragmentManager.getFragmentFilter();
        TreeSet<String> groups = new TreeSet<>(fragmentFilter.keySet());
        for (String group : groups) {
            Checkbox filterEnabled = new Checkbox();
            filterEnabled.addItemListener(e -> {
                fragmentManager.putFragmentFilter(e.getItem().toString(),
                        e.getStateChange() == ItemEvent.SELECTED);
                handleDocumentEvent(searchField.getText());
            });
            filterEnabled.setLabel(group);
            filterEnabled.setState(fragmentFilter.get(group));
            filterPanel.add(filterEnabled);
        }

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
                                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(filterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(filterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(recomCodePanelWrapper, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                                .addContainerGap())
        );

        logger.debug("Recommender Panel created");
    }

    /**
     * puts focus ito search field
     */
    public void focusIn() {
        searchField.requestFocus();
    }

    /**
     * gets the user input from document events
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

        handleDocumentEvent(input);
    }

    /**
     * handles the events of user input
     */
    private void handleDocumentEvent(String input) {
        if (input == null) {
            return;
        }
        CodeFragmentManager fragmentManager = CodeFragmentManager.getInstance(openedProject);
        fragmentManager.onInput(input);
        updateAndDisplayRecommendations();

    }

    /**
     * This method should be called to update the list of recommendations.
     * It takes care of getting recommendations from worker
     * It replaces the usage of the CodeFragmentListener
     * <p>
     * for each user input insert the following afterwards:
     * RecomCodeManager recomCodeManager = RecomCodeManager.getInstance(project);
     * recomCodeManager.updateAndDisplayRecommendations();
     */
    public void updateAndDisplayRecommendations() {
        logger.debug("updating recommendations..");
        CodeFragmentManager fragmentManager = CodeFragmentManager.getInstance(openedProject);
        LinkedHashSet<CodeFragment> newRecommendations = fragmentManager.getRecommendations();
        EventQueue.invokeLater(() -> repaintRecommendations(fragmentManager, newRecommendations));
    }

    /**
     * Code which repaints recommendations, and adds listener to each recommended fragement for mause click
     *
     * @param fragmentManager current CodeFragmentManager
     * @param fragments  set of current fragments
     */
    private void repaintRecommendations(CodeFragmentManager fragmentManager,
                                        LinkedHashSet<CodeFragment> fragments) {
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
                    logger.debug(String.format("template for fragment '%s' selected", fragment.getRecID()));
                    fragmentManager.onCodeFragment(fragment);

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

        EventQueue.invokeLater(() -> resizeRecomCodePanel());
    }

    private void resizeRecomCodePanel() {
        Component lastComponent = recomCodePanel.getComponent(recomCodePanel.getComponentCount() - 1);
        int height = lastComponent.getY() + lastComponent.getHeight() + 5;
        recomCodePanel.setPreferredSize(new Dimension(recomCodePanel.getWidth(), height));
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
                if (p.hasExpr()) {
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
