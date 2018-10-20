package de.pavloff.pycharm.plugin.macros;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.jetbrains.python.codeInsight.controlflow.ControlFlowCache;
import com.jetbrains.python.codeInsight.controlflow.ScopeOwner;
import com.jetbrains.python.codeInsight.dataflow.scope.Scope;
import com.jetbrains.python.codeInsight.dataflow.scope.ScopeUtil;
import com.jetbrains.python.codeInsight.liveTemplates.PyIterableVariableMacro;
import com.jetbrains.python.psi.PyImplicitImportNameDefiner;
import com.jetbrains.python.psi.PyTypedElement;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.StreamSupport;

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

//        final TypeEvalContext typeEvalContext =
//                TypeEvalContext.userInitiated(element.getProject(), element.getContainingFile());

        for (PsiNamedElement namedElement : getVisibleNamedElements(element)) {
            if (namedElement instanceof PyTypedElement) {
                String elementName = namedElement.getName();

                if (elementNames.contains(elementName)) {
                    continue;
                }

                // FIXME: type checking takes a lot of time
//                PyType type = typeEvalContext.getType((PyTypedElement)namedElement)
//                if (type != null && PyABCUtil.isSubtype(type, PyNames.ITERABLE,
//                        typeEvalContext)) {
                    elementNames.add(elementName);
                    uniqueComponents.add(namedElement);
//                }
            }
        }

        return uniqueComponents;
    }

    @NotNull
    private static List<PsiNamedElement> getVisibleNamedElements(@NotNull PsiElement anchor) {
        final List<PsiNamedElement> results = new ArrayList<>();

        for (ScopeOwner owner = ScopeUtil.getScopeOwner(anchor); owner != null; owner = ScopeUtil.getScopeOwner(owner)) {
            final Scope scope = ControlFlowCache.getScope(owner);
            results.addAll(scope.getNamedElements());

            StreamEx
                    .of(scope.getImportedNameDefiners())
                    .filter(definer -> !(definer instanceof PyImplicitImportNameDefiner))
                    .flatMap(definer -> StreamSupport.stream(definer.iterateNames().spliterator(), false))
                    .select(PsiNamedElement.class)
                    .forEach(results::add);
        }
        return results;
    }
}
