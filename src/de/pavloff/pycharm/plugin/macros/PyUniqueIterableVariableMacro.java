package de.pavloff.pycharm.plugin.macros;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.jetbrains.python.codeInsight.liveTemplates.PyIterableVariableMacro;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PyUniqueIterableVariableMacro extends PyIterableVariableMacro {

    @Override
    public String getName() {
        return "pyUniqueIterableVariable";
    }

    @Override
    public String getPresentableName() {
        return "pyUniqueIterableVariable()";
    }

    @NotNull
    protected List<PsiNamedElement> getIterableElements(@NotNull PsiElement element) {
        List<String> elementNames = new LinkedList<>();
        List<PsiNamedElement> uniqueComponents = new ArrayList<>();

        for (PsiNamedElement component : super.getIterableElements(element)) {
            String elementName = component.getName();
            if (elementNames.contains(elementName)) {
                continue;
            }
            elementNames.add(elementName);
            uniqueComponents.add(component);
        }

        return uniqueComponents;
    }
}
