package com.weirddev.testme.intellij.action.muti;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.testIntegration.GotoTestOrCodeHandler;
import com.intellij.testIntegration.TestFinderHelper;
import com.intellij.util.SmartList;
import com.weirddev.testme.intellij.TestMeBundle;
import com.weirddev.testme.intellij.action.CreateTestMeAction;
import com.weirddev.testme.intellij.template.TemplateDescriptor;
import com.weirddev.testme.intellij.template.TemplateRegistry;
import com.weirddev.testme.intellij.ui.popup.ConfigurationLinkAction;
import com.weirddev.testme.intellij.ui.template.TestMeTemplateManager;
import com.weirddev.testme.intellij.utils.TestSubjectResolverUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 10/15/2016
 *
 * @author Yaron Yamin
 * @see GotoTestOrCodeHandler
 */
public class MutiTestMeActionHandler extends MutiTestMePopUpHandler {
    private TemplateRegistry templateRegistry;

    public MutiTestMeActionHandler() {
        this(new TemplateRegistry());
    }

    MutiTestMeActionHandler(TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
    }

    //    @NotNull
    private static PsiElement getSelectedElement(Editor editor, PsiFile file) {
        return PsiUtilCore.getElementAtOffset(file, editor.getCaretModel().getOffset());
    }

    @Nullable
    protected com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.GotoData getSourceAndTargetElements(final DataContext dataContext, final List<PsiFile> file) {
//        PsiElement sourceElement = TestFinderHelper.findSourceElement(/*getSelectedElement(editor, file)*/ file);
//        if (sourceElement == null) return null;
        //过滤不合要求的
        List<PsiFile> sourcePsiFiles = file.stream()
                .filter(x -> TestFinderHelper.findSourceElement(x) != null)
                .collect(Collectors.toList());
        if (sourcePsiFiles.size() == 0) {
            return null;
        }
        List<com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction> actions = new SmartList<com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction>();
//        findNestedClassName(file, (PsiNamedElement) sourceElement);
        TestMeTemplateManager fileTemplateManager = TestMeTemplateManager.getInstance(CommonDataKeys.PROJECT.getData(dataContext));
        List<TemplateDescriptor> templateDescriptors = fileTemplateManager.getTestTemplates();
        for (final TemplateDescriptor templateDescriptor : templateDescriptors) {
            actions.add(new MutiTestMeAdditionalActionList(templateDescriptor, dataContext, file));
        }
        actions.add(new ConfigurationLinkAction());
        //下面这行先用于兼容 ， todo 再考虑Fix
        var sourceElement = sourcePsiFiles.get(0);
        return new com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.GotoData(sourceElement, actions);
    }


    @Override
    protected String getChooserTitle(List<PsiFile> file, PsiElement sourceElement) {
        if (file.size() > 0) {
            return TestMeBundle.message("testMe.create.title", "多个File " + file.size());

        }
        PsiNamedElement namedElement = (PsiNamedElement) sourceElement;
        final String name = namedElement.getName();
        String nestedClassName = null;// findNestedClassName(editor, file, namedElement);
        return TestMeBundle.message("testMe.create.title", nestedClassName != null ? nestedClassName : name);
    }

    private String findNestedClassName(PsiFile file, PsiNamedElement sourceElement) {
        String alternativeSourceName = null;
        PsiElement element = TestSubjectResolverUtils.getTestableElement(file);
        if (element != null) {
            PsiClass containingClass = CreateTestMeAction.getContainingClass(element);
            if (containingClass != null) {
                final String name = sourceElement.getName();
                if (containingClass.getName() != null && !containingClass.getName().equals(name)) {
                    alternativeSourceName = containingClass.getName();
                }
            }
        }
        return alternativeSourceName;
    }

    @Override
    protected String getFeatureUsedKey() {
        return "TestMe.generate.test"; //todo - map key. see lazyLoadFromPluginsFeaturesProviders()
    }

    @NotNull
    @Override
    protected String getNotFoundMessage(@NotNull Project project, @NotNull DataContext editor, @NotNull PsiFile file) {
        return CodeInsightBundle.message("goto.test.notFound");
    }

    @Nullable
    @Override
    protected String getAdText(PsiElement source, int length) {//todo might be useful for generate and run functionality, currently un-used
//        if (length > 0 && !TestFinderHelper.isTest(source)) {
//            final Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
//            final Shortcut[] shortcuts = keymap.getShortcuts(DefaultRunExecutor.getRunExecutorInstance().getContextActionId());
//            if (shortcuts.length > 0) {
//                return ("Press " + KeymapUtil.getShortcutText(shortcuts[0]) + " to run selected tests");
//            }
//        }
        return null;
    }

    @Override
    protected void navigateToElement(Navigatable element) {
        if (element instanceof PsiElement) {
            NavigationUtil.activateFileWithPsiElement((PsiElement) element, true);
        } else {
            element.navigate(true);
        }
    }
}
