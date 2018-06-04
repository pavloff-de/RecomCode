package de.pavloff.recomcode.core;

import com.intellij.openapi.project.Project;

import javax.swing.event.DocumentEvent;
import java.util.LinkedList;
import java.util.List;

public class CodeFragmentManager {

    private List<CodeFragmentListener> codeFragmentListeners = new LinkedList<>();
    private List<CodeParamListener> codeParamListeners = new LinkedList<>();

    public static CodeFragmentManager getInstance(Project project) {
        return project.getComponent(CodeFragmentManager.class);
    }

    public void addCodeFragmentListener(CodeFragmentListener listener) {
        codeFragmentListeners.add(listener);
    }

    public void addCodeParamListener(CodeParamListener listener) {
        codeParamListeners.add(listener);
    }

    public void handleDocumentEvent(DocumentEvent e) {

    }
}
