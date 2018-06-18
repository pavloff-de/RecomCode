package de.pavloff.pycharm.plugin.varviewer;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.pavloff.pycharm.ipnb.ConnectionManager;
import de.pavloff.pycharm.ipnb.OutputCell;
import de.pavloff.pycharm.plugin.BaseConstants;
import org.jetbrains.plugins.ipnb.editor.panels.code.IpnbErrorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class VarViewerManager implements BaseConstants {

    private JBTabbedPane tabbedPane;
    private Project openedProject;

    public static VarViewerManager getInstance(Project project) {
        return project.getComponent(VarViewerManager.class);
    }

    JComponent initView(Project project) {
        openedProject = project;

        JPanel mainPanel = new JPanel();

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

        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("VarViewer.Toolbar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("VarViewer.Toolbar.ID", actionGroup, true);

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPane)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(actionToolbar.getComponent(), 40, 40, Short.MAX_VALUE)
                                                .addGap(0, 100, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(actionToolbar.getComponent(), 40, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, 230, 230, Short.MAX_VALUE)
                                .addContainerGap())
        );

        initConn();

        return mainPanel;
    }

    public void executeCode() {
        VirtualFile openedFile = getOpenedFile();

        if (openedFile == null) {
            return;
        }

        FileDocumentManager fileManager = FileDocumentManager.getInstance();
        Document doc = fileManager.getDocument(openedFile);

        if (doc == null) {
            return;
        }

        int cursorPosition = getCursorPosition();
        if (cursorPosition == 0) {
            return;
        }

        StringBuilder codeContent = new StringBuilder();

        codeContent.append(doc.getText(new TextRange(0, doc.getLineEndOffset(cursorPosition))));
        codeContent.append(LINE_SEP);
        codeContent.append(String.format("print '%s'", VAR_VIEWER_SEP)).append(LINE_SEP);

        URL resources = VarViewerManager.class.getResource("python/var_viewer.py");
        try {
            StringBuilder content = new StringBuilder();
            BufferedInputStream in = (BufferedInputStream) resources.getContent();
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
        ipnb.execute(openedFile, code, new OutputCell() {
            @Override
            public void onOutput(List<String> output) {
                createTabs(output);
            }

            @Override
            public void onPayload(String payload) {
                // ???
                createTabs(Collections.singletonList(payload));
            }

            @Override
            public void onError(String eName, String eValue, List<String> traceback) {
                tabbedPane.removeAll();
                tabbedPane.addTab(OUTPUT_TAB, createTabPanel(IpnbErrorPanel.createColoredPanel(traceback)));
            }
        });
    }

    private void initConn() {
        VirtualFile openedFile = getOpenedFile();

        // FileEditorManagerListener
        // fileClosed
        // selectionChanged

        // DocumentListener
        // documentChanged

        if (openedFile == null) {
            return;
        }
        ConnectionManager ipnb = ConnectionManager.getInstance(openedProject);
        ipnb.initConnection(openedFile);
    }

    private FileEditor getOpenedEditor() {
        FileEditor[] editors = FileEditorManager.getInstance(openedProject).getSelectedEditors();

        if (editors.length == 1) {
            return editors[0];
        }

        return null;
    }

    private VirtualFile getOpenedFile() {
        FileEditor editor = getOpenedEditor();

        if (editor == null) {
            return null;
        }
        return editor.getFile();
    }

    private int getCursorPosition() {
        FileEditor editor = getOpenedEditor();
        if (editor == null) {
            return -1;
        }

        TextEditorLocation location = (TextEditorLocation) editor.getCurrentLocation();
        if (location == null) {
            return -1;
        }

        return location.getPosition().line;
    }

    private void onTabOpen() {
        int tabIdx = tabbedPane.getSelectedIndex();
        if (tabIdx < 0) {
            return;
        }

        String tabName = tabbedPane.getTitleAt(tabIdx);
        if (tabName.equals(OUTPUT_TAB)) {
            return;
        }

        Component comp = tabbedPane.getComponentAt(tabIdx);
        if (comp instanceof DataframeTab) {
            ((DataframeTab) comp).open(openedProject, getOpenedFile(), tabName);
        }
    }

    private JComponent createTabPanel(String output) {
        JTextArea textArea = new JTextArea(output);
        textArea.setEditable(false);
        return createTabPanel(textArea);
    }

    private JComponent createTabPanel(JComponent panel) {
        panel.setLayout(new BorderLayout());
        return new JBScrollPane(panel);
    }

    private void createTabs(List<String> fromIpnb) {
        String output;
        if (fromIpnb.size() != 1) {
            output = String.join(LINE_SEP, fromIpnb);
        } else {
            output = fromIpnb.get(0);
        }

        String[] outputLines = output.split(LINE_SEP);
        StringBuilder mainOutput = new StringBuilder();
        LinkedList<String> dfOutput = new LinkedList<>();
        boolean varViewerOutputFound = false;

        for (String s : outputLines) {
            if (s.startsWith(VAR_VIEWER_SEP)) {
                varViewerOutputFound = true;

            } else if (varViewerOutputFound) {
                if (s.startsWith("DataFrame ")) {
                    dfOutput.add(s);
                }
            } else {
                mainOutput.append(s).append(LINE_SEP);
            }
        }

        int openedTab = tabbedPane.getSelectedIndex();
        if (openedTab < 0) {
            openedTab = 0;
        }

        tabbedPane.removeAll();
        tabbedPane.insertTab(OUTPUT_TAB, null, createTabPanel(mainOutput.toString()), null, 0);
        for (String s : dfOutput) {
            tabbedPane.addTab(s, new DataframeTab());
        }

        try {
            tabbedPane.setSelectedIndex(openedTab);
        } catch (IndexOutOfBoundsException ignored) {
            tabbedPane.setSelectedIndex(0);
        }

        tabbedPane.getParent().revalidate();
    }

    private void decouple(DataframeTab tab, int tabIdx) {
        String tabName = tabbedPane.getTitleAt(tabIdx);

        final JFrame popup = new JFrame();
        popup.add(tab);
        popup.setSize(800, 500);
        popup.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                tabbedPane.insertTab(tabName, null, tab, null, Math.min(tabIdx, tabbedPane.getTabCount()));
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
}
