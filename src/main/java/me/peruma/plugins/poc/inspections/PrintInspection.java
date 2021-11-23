package me.peruma.plugins.poc.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import me.peruma.plugins.poc.common.PluginResourceBundle;

import java.util.Objects;

public class PrintInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String DESCRIPTION =
            PluginResourceBundle.message(PluginResourceBundle.Type.INSPECTION,"inspection.smell.print.description");

    /**
     * DO NOT OVERRIDE this method.
     *
     * @see InspectionEP#enabledByDefault
     */
    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    /**
     * @see InspectionEP#displayName
     * @see InspectionEP#key
     * @see InspectionEP#bundle
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDisplayName() {
        return PluginResourceBundle.message(PluginResourceBundle.Type.INSPECTION, "inspection.smell.print.name.display");
    }

    /**
     * DO NOT OVERRIDE this method.
     *
     * @see InspectionEP#shortName
     */
    @Override
    public @NonNls @NotNull String getShortName() {
        return PluginResourceBundle.message(PluginResourceBundle.Type.INSPECTION,"inspection.smell.print.name.short");
    }

    /**
     * @see InspectionEP#groupDisplayName
     * @see InspectionEP#groupKey
     * @see InspectionEP#groupBundle
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getGroupDisplayName() {
        return "JavaTestSmells";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                if (expression.getMethodExpression().getQualifierExpression() == null)
                    return;
                PsiType s = expression.getMethodExpression().getQualifierExpression().getType();
                if (s == null)
                    return;
                boolean match = s.getCanonicalText().equals("java.io.PrintStream");
                boolean match2 = Objects.equals(expression.getMethodExpression().getReferenceName(), "println");
                if (match || match2) {
                    System.out.println(expression.getMethodExpression().getReferenceName());
                    holder.registerProblem(expression, DESCRIPTION, new QuickFixRemove(), new QuickFixComment());
                }
            }

        };
    }

    private static class QuickFixRemove implements LocalQuickFix {
        /**
         * @return true if this quick-fix should not be automatically filtered out when running inspections in the batch mode.
         * Fixes that require editor or display UI should return false. It's not harmful to return true if the fix is never
         * registered in the batch mode (e.g. {@link ProblemsHolder#isOnTheFly()} is checked at the fix creation site).
         */
        @Override
        public boolean availableInBatchMode() {
            return false;
        }

        /**
         * @return the name of the quick fix.
         */
        @Override
        public @IntentionName @NotNull String getName() {
            return PluginResourceBundle.message(PluginResourceBundle.Type.INSPECTION,"inspection.smell.print.fix.remove");
        }

        /**
         * Called to apply the fix.
         * <p>
         * Please call {@link ProjectInspectionProfileManager#fireProfileChanged()} if inspection profile is changed as result of fix.
         *
         * @param project    {@link Project}
         * @param descriptor problem reported by the tool which provided this quick fix action
         */
        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                descriptor.getPsiElement().delete();
            } catch (IncorrectOperationException e) {
                System.out.println(e);
            }
        }

        /**
         * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
         * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
         * If the name of the quickfix does not depend on a specific element, simply return {@link #getName()}.
         */
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }
    }

    private static class QuickFixComment implements LocalQuickFix {
        /**
         * @return true if this quick-fix should not be automatically filtered out when running inspections in the batch mode.
         * Fixes that require editor or display UI should return false. It's not harmful to return true if the fix is never
         * registered in the batch mode (e.g. {@link ProblemsHolder#isOnTheFly()} is checked at the fix creation site).
         */
        @Override
        public boolean availableInBatchMode() {
            return false;
        }

        /**
         * @return the name of the quick fix.
         */
        @Override
        public @IntentionName @NotNull String getName() {
            return PluginResourceBundle.message(PluginResourceBundle.Type.INSPECTION,"inspection.smell.print.fix.comment");
        }

        /**
         * Called to apply the fix.
         * <p>
         * Please call {@link ProjectInspectionProfileManager#fireProfileChanged()} if inspection profile is changed as result of fix.
         *
         * @param project    {@link Project}
         * @param descriptor problem reported by the tool which provided this quick fix action
         */
        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            // Note: I haven't as yet figured out how to comment a PsiElement. So, for now writing a copy of the PsiElement as a comment next to the actual PsiElement
            try {
                String psiText = "/* " + descriptor.getPsiElement().getText() + " */";
                PsiComment pisComment = JavaPsiFacade.getElementFactory(project).createCommentFromText(psiText, null);
                descriptor.getPsiElement().add(pisComment);
            } catch (IncorrectOperationException e) {
                System.out.println(e);
            }
        }


        /**
         * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
         * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
         * If the name of the quickfix does not depend on a specific element, simply return {@link #getName()}.
         */
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }
    }

}
