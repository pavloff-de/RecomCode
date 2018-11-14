package de.pavloff.pycharm.plugin.macros;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyElementGeneratorImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Simple macro which can be used in live templates as a placeholder with a fixed list
 * of possible variables
 * It generates PsiElements from a given list of variables
 */
public class PyVariableMacro extends Macro {

    private String[] vars;

    public PyVariableMacro(String[] varExamples) {
        vars = varExamples;
    }

    @Override
    public String getName() {
        return "PyVariable";
    }

    @Override
    public String getPresentableName() {
        return "PyVariable()";
    }

    /**
     * returns the first possible variable to preset this in a live template
     */
    @Nullable
    @Override
    public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
        PsiElement[] elements = getElements(context);
        return new PsiElementResult(elements[0]);
    }

    /**
     * calculates all possible variables to show the suggestion in a live template
     */
    @Nullable
    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression[] params, ExpressionContext context) {
        PsiElement[] pyElements = getElements(context);
        LookupElement[] lookupElements = new LookupElement[pyElements.length];
        for (int i = 0; i < pyElements.length; i++) {
            if (vars[i].length() == 0) {
                continue;
            }
            lookupElements[i] = LookupElementBuilder.createWithSmartPointer(vars[i], pyElements[i]);
        }
        return lookupElements;
    }

    /**
     * generates PsiElements from the list of variables
     */
    private PsiElement[] getElements(ExpressionContext context) {
        PsiElement[] elements = new PsiElement[vars.length];
        PyElementGeneratorImpl generator = new PyElementGeneratorImpl(context.getProject());
        for (int i = 0; i < vars.length; i++) {
            if (vars[i].length() == 0) {
                continue;
            }
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
