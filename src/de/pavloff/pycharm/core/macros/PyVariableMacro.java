package de.pavloff.pycharm.core.macros;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyElementGeneratorImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PyVariableMacro extends Macro {

    private String[] vars;

    public PyVariableMacro(String[] varExamples) {
        vars = varExamples;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getPresentableName() {
        return null;
    }

    @Nullable
    @Override
    public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
        PsiElement[] elements = getElements(context);
        return new PsiElementResult(elements[0]);
    }

    @Nullable
    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression[] params, ExpressionContext context) {
        PsiElement[] pyElements = getElements(context);
        LookupElement[] lookupElements = new LookupElement[pyElements.length];
        for (int i = 0; i < pyElements.length; i++) {
            lookupElements[i] = LookupElementBuilder.createWithSmartPointer(vars[i], pyElements[i]);
        }
        return lookupElements;
    }

    private PsiElement[] getElements(ExpressionContext context) {
        PsiElement[] elements = new PsiElement[vars.length];
        PyElementGeneratorImpl generator = new PyElementGeneratorImpl(context.getProject());
        for (int i = 0; i < vars.length; i++) {
            try{
                elements[i] = generator.createFromText(
                        LanguageLevel.PYTHON26, PyExpressionStatement.class, vars[i]);
            } catch (IllegalArgumentException ignored) {
                elements[i] = generator.createFromText(
                        LanguageLevel.PYTHON26, LeafPsiElement.class, vars[i]);
            }
        }
        return elements;
    }
}
