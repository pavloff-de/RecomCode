package de.pavloff.pycharm.core;

import com.intellij.openapi.project.Project;
import de.pavloff.pycharm.yaml.YamlLoader;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.util.LinkedList;
import java.util.List;

public class CodeFragmentManager {

    private CodeFragmentLoader loader = new YamlLoader();

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
        String input = "";
        try {
            input = e.getDocument().getText(0, e.getOffset());
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        List<CodeFragment> recommendation = new LinkedList<>();
        String[] keywords = getKeywordsFrom(input);
        for (CodeFragment fragment : loader.getCodeFragments(null)) {
            for (String keyword : keywords) {
                if (fragment.containsKeyword(keyword)) {
                    recommendation.add(fragment);
                }
            }
        }

        returnRecommendations(recommendation);
    }

    private String[] getKeywordsFrom(String input) {
        return input.split(" ");
    }

    private void returnRecommendations(List<CodeFragment> recommendation) {
        for (CodeFragmentListener listener : codeFragmentListeners) {
            listener.onOutput(recommendation);
        }
    }
}
