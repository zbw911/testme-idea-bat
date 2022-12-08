package com.weirddev.testme.intellij.action.muti;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testIntegration.JavaTestCreator;
import com.intellij.util.IncorrectOperationException;
import com.weirddev.testme.intellij.template.TemplateDescriptor;
import com.weirddev.testme.intellij.utils.TestSubjectResolverUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 10/18/2016
 *
 * @author Yaron Yamin
 * @see JavaTestCreator
 */
public class MutiTestMeCreator {
    private static final Logger LOG = Logger.getInstance(MutiTestMeCreator.class.getName());

    public void createTest(DataContext editor, PsiFile file, TemplateDescriptor templateDescriptor) {
        try {
            invoke(file.getProject(), editor, file.getContainingFile(), templateDescriptor);
        } catch (IncorrectOperationException e) {
            LOG.warn(e);
        }
    }

    private void invoke(@NotNull Project project, DataContext editor, PsiFile file, TemplateDescriptor templateDescriptor) throws IncorrectOperationException {
        if (!file.getManager().isInProject(file)) return;
        final PsiElement element = TestSubjectResolverUtils.getTestableElement(file);
        if (element != null) {
            new MutiCreateTestMeAction(templateDescriptor).invoke(project, null, element);
        }
    }

}
