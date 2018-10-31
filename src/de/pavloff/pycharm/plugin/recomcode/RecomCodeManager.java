package de.pavloff.pycharm.plugin.recomcode;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.impl.ComponentManagerImpl;
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
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;      // output in In ${idea.system.path}/log/idea.log
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;


/** Implements the plugin component which manages the recommendations, and contains the main "event handling" for recommendations.
 * Implements the ProjectComponent interface, see https://goo.gl/kjpXga
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under <project-components>.
 */

public class RecomCodeManager implements ProjectComponent {

    private JBTextField searchField;
    private JPanel recomCodePanel;
    private Project openedProject;
    static Logger logger = Logger.getInstance(RecomCodeManager.class);



    public RecomCodeManager (Project inputProject) {
        openedProject = inputProject;
    }

    public static RecomCodeManager createAndRegisterInstance (Project project) {
        MutablePicoContainer container = (MutablePicoContainer) project.getPicoContainer();

        RecomCodeManager manager = new RecomCodeManager(project);
        container.registerComponentInstance(manager);

        RecomCodeManager managerFromContainer = project.getComponent(RecomCodeManager.class);

        logger.info("MutablePicoContainer instance is: " + container);
        logger.info("managerFromContainer  is: " + managerFromContainer);
        return manager;
/*

        Iterator var3 = container.getComponentAdapters().iterator();
        while(var3.hasNext()) {
            ComponentAdapter componentAdapter = (ComponentAdapter)var3.next();
            if (componentAdapter instanceof ComponentManagerImpl.ComponentConfigComponentAdapter) {
                componentAdapter.getComponentInstance(container);
            }
        }

*/
    }

    @Override
    public void initComponent() {
        ServerStub server = ServerStub.getInstance(openedProject);
        server.initialize(openedProject);

        logger.info("RecomCodeManager initialized");
    }

    @Override
    public void disposeComponent() {
        // called when project is disposed
        logger.debug("RecomCodeManager disposed");
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RecomCode.RecomCodeManager";
    }

    @Override
    public void projectOpened() {
        // called when project is opened
    }

    @Override
    public void projectClosed() {
        // called when project is being closed
    }




    public static RecomCodeManager getInstance(Project project) {
        RecomCodeManager manager = project.getComponent(RecomCodeManager.class);
        return manager;
    }

    // todo: possibly move this method to RecomCodeToolWindow, as the other class is responsible for the panel (what about instance vars?)
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
            RecomBox r = new RecomBox(fragment);

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
