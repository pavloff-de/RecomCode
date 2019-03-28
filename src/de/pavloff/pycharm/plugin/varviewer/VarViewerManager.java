package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.pavloff.pycharm.BaseUtils;
import de.pavloff.pycharm.core.CodeFragmentManager;
import de.pavloff.pycharm.core.CodeVariable;
import de.pavloff.pycharm.plugin.ipnb.ConnectionManager;
import org.jetbrains.plugins.ipnb.editor.panels.code.IpnbErrorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/** Implements the plugin component which manages the variables initialized in python
 * code, executes the code and handles the output.
 * Implements the ProjectComponent interface, see https://goo.gl/kjpXga
 * It is instantiated directly by IDEA/PyCharm, since listed in plugin.xml under
 * <project-components>.
 * It executes python code with Jupyter Notebook and display dataframes in tabs, see
 * {@link DataframeTab}
 */

public class VarViewerManager implements ProjectComponent {

    private JPanel toolWindow;

    private JBTabbedPane tabbedPane = new JBTabbedPane();

    private Project openedProject;

    private static Logger logger = Logger.getInstance(VarViewerManager.class);

    public VarViewerManager(Project project) {
        openedProject = project;

        createViewerPanel();

        // creates connection to Jupyter Notebook
        VirtualFile openedFile = BaseUtils.getOpenedFile(openedProject);
        if (openedFile != null) {
            ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
            ipnb.initConnection(openedFile);
        } else {
            logger.debug("initializing of connection to Jupyter Notebook skipped");
        }
    }

    public static VarViewerManager getInstance(Project project) {
        return project.getComponent(VarViewerManager.class);
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
     * viewer panel contains
     * button to execute python code
     * tabbedPane to display the dataframes
     */
    private void createViewerPanel() {
        logger.debug("creating VarViewer Panel..");

        JButton executeButton = new JButton();
        executeButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode();
            }
        });
        try {
            Image img = ImageIO.read(BaseUtils.getResource("/img/play.png"));
            executeButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            executeButton.setText("Run");
        }

        tabbedPane = new JBTabbedPane();
        tabbedPane.addChangeListener(e -> onTabOpen());
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int tabIdx = tabbedPane.indexAtLocation(evt.getX(), evt.getY());
                    Component comp = tabbedPane.getComponentAt(tabIdx);

                    if (comp instanceof DataframeTab) {
                        decouple((DataframeTab) comp, tabIdx);
                    }
                }
            }
        });

        toolWindow = new JPanel();
        GroupLayout layout = new GroupLayout(toolWindow);
        toolWindow.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPane)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(executeButton, 40, 40, 40)
                                                .addGap(0, 100, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(executeButton, 40, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, 230, 230, Short.MAX_VALUE)
                                .addContainerGap())
        );

        logger.debug("VarViewer Panel created");
    }

    /**
     * gets python code from currently open editor and
     * executes it with Jupyter Notebook until the line with cursor
     */
    public void executeCode() {
        logger.debug("code executing..");
        VirtualFile openedFile = BaseUtils.getOpenedFile(openedProject);

        if (openedFile == null) {
            logger.debug("no opened python file found");
            return;
        }

        FileDocumentManager fileManager = FileDocumentManager.getInstance();
        Document doc = fileManager.getDocument(openedFile);

        if (doc == null) {
            logger.debug("no document in open file found");
            return;
        }

        int cursorPosition = BaseUtils.getCursorPosition(openedProject);
        if (cursorPosition == 0) {
            logger.debug(String.format("nothing to execute. cursor position %s",
                    cursorPosition));
            return;
        }

        StringBuilder codeContent = new StringBuilder();

        codeContent.append(doc.getText(new TextRange(0, doc.getLineEndOffset(cursorPosition))));
        codeContent.append(BaseUtils.LINE_SEP);
        codeContent.append(String.format("print('%s')", BaseUtils.VAR_VIEWER_SEP)).append(BaseUtils.LINE_SEP);

        URL resources = BaseUtils.getResource("/python/var_viewer.py");
        StringBuilder content = new StringBuilder();
        try {
            FilterInputStream in = (FilterInputStream) resources.getContent();
            int bytesRead;
            byte[] bytes = new byte[256];
            while((bytesRead = in.read(bytes)) != -1) {
                content.append(new String(bytes, 0, bytesRead));
            }

            codeContent.append(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String code = codeContent.toString();
        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
        ipnb.execute(openedFile, code, new ConnectionManager.Output() {
            @Override
            public void onFinished(List<String> o, List<String> e, List<String> p) {
                evaluateOutput(o, e, p);
            }
        });
    }

    /**
     * parses the output from Jupyter Notebook and
     * creates tabs in tabbedPane.
     *
     * the first tab contains always the output of code
     * other tabs contains the pandas dataframes, initialized in code
     */
    private void evaluateOutput(List<String> output, List<String> traceback,
                                List<String> payload) {
        logger.debug("evaluating output..");

        StringBuilder outputBuilder = new StringBuilder();
        LinkedList<String> dfOutput = new LinkedList<>();
        Map<String, CodeVariable> varOutput = new HashMap<>();
        boolean varViewerOutputFound = false;

        if (output != null) {
            for (String line : output) {
                for (String s : line.split(BaseUtils.LINE_SEP)) {
                    if (s.startsWith(BaseUtils.VAR_VIEWER_SEP)) {
                        varViewerOutputFound = true;

                        if (payload != null) {
                            for (String pLine : payload) {
                                for (String p : pLine.split(BaseUtils.LINE_SEP)) {
                                    outputBuilder.append(p).append(BaseUtils.LINE_SEP);
                                }
                            }
                        }

                    } else if (varViewerOutputFound) {
                        logger.debug("parsing output..");

                        String[] vars = s.split(" ");
                        String varType = null;
                        String varName = null;
                        String moduleName = null;

                        if (vars.length > 0) {
                            varType = vars[0];
                        }
                        if (vars.length > 1) {
                            varName = vars[1];
                        }
                        if (vars.length > 2) {
                            moduleName = vars[2];
                        }

                        varOutput.put(varName, new CodeVariable.Builder()
                                .setType(varType).setName(varName).setModuleName(moduleName).build());

                        if (varType != null && varType.equals("DataFrame")) {
                            dfOutput.add(s);
                        }

                    } else {
                        outputBuilder.append(s).append(BaseUtils.LINE_SEP);
                    }
                }
            }

        }

        createTabs(outputBuilder, dfOutput, traceback);

        CodeFragmentManager fragmentManager = CodeFragmentManager.getInstance(openedProject);
        fragmentManager.onVariables(varOutput);
    }

    /**
     * displays the content of a tab
     */
    private void onTabOpen() {
        int tabIdx = tabbedPane.getSelectedIndex();
        logger.debug(String.format("opening tab idx %s..", tabIdx));
        if (tabIdx < 0) {
            return;
        }

        String tabName = tabbedPane.getTitleAt(tabIdx);
        logger.debug(String.format("..tab name '%s'", tabName));
        if (tabName.equals(BaseUtils.OUTPUT_TAB)) {
            return;
        }

        Component comp = tabbedPane.getComponentAt(tabIdx);
        if (comp instanceof DataframeTab) {
            ((DataframeTab) comp).open(openedProject, BaseUtils.getOpenedFile(openedProject),
                    tabName);
        } else {
            logger.debug(String.format("..tab component '%s'", comp.getClass()));
        }
    }

    /**
     * puts tab content in a scroll pane
     */
    private JBScrollPane createTabPanel(JComponent panel) {
        panel.setLayout(new BorderLayout());
        return new JBScrollPane(panel);
    }

    /**
     * creates tab content from text and puts it in a scroll pane
     */
    private JBScrollPane createTabPanel(String output) {
        JTextArea textArea = new JTextArea(output);
        textArea.setEditable(false);
        return createTabPanel(textArea);
    }

    /**
     * creates tabs for output and dataframes
     */
    private void createTabs(StringBuilder mainOutput, LinkedList<String> dfOutput, List<String> traceback) {
        logger.debug("creating tabs..");
        int openedTab = tabbedPane.getSelectedIndex();
        if (openedTab < 0) {
            openedTab = 0;
        }
        logger.debug(String.format("previously selected tab was %s..", openedTab));

        tabbedPane.removeAll();

        logger.debug("adding output tab..");

        JBScrollPane outputTab;
        if (traceback != null) {
            traceback.add(0, mainOutput.toString());
            outputTab = createTabPanel(IpnbErrorPanel.createColoredPanel(traceback));
        } else {
            outputTab = createTabPanel(mainOutput.toString());
        }
        tabbedPane.insertTab(BaseUtils.OUTPUT_TAB, null, outputTab, null, 0);

        logger.debug("adding dataframe tabs..");
        for (String s : dfOutput) {
            tabbedPane.addTab(s, new DataframeTab());
        }

        logger.debug(String.format("selecting tab %s..", openedTab));
        try {
            tabbedPane.setSelectedIndex(openedTab);
        } catch (IndexOutOfBoundsException err) {
            logger.debug(String.format("%s. selecting tab 0..", err.getMessage()));
            tabbedPane.setSelectedIndex(0);
        }

        tabbedPane.getParent().revalidate();
    }

    /**
     * opens dataframe in a new popup window
     */
    private void decouple(DataframeTab tab, int tabIdx) {
        String tabName = tabbedPane.getTitleAt(tabIdx);
        logger.debug(String.format("opening tab %s in a popup window..", tabIdx));

        final JFrame popup = new JFrame();
        popup.add(tab);
        tab.setBorder(BorderFactory.createEmptyBorder());
        popup.setSize(800, 500);
        popup.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                logger.debug(String.format("closing popup window with tab %s..", tabIdx));
                tabbedPane.insertTab(tabName, null, tab, null, Math.min(tabIdx,
                        tabbedPane.getTabCount()));
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        popup.setVisible(true);
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
