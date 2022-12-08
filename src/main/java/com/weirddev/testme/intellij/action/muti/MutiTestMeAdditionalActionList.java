package com.weirddev.testme.intellij.action.muti;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.weirddev.testme.intellij.template.TemplateDescriptor;
import com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;


/**
 * Date: 10/15/2016
 *
 * @author zhangbaowei
 */
public class MutiTestMeAdditionalActionList implements TestMePopUpHandler.AdditionalAction {

    private final TemplateDescriptor templateDescriptor;
    private final DataContext editor;
    private final List<PsiFile> psiFiles;
    private final String text;
    private final MutiTestMeCreator testMeCreator;
    private final String tokenizedtext;

    public MutiTestMeAdditionalActionList(TemplateDescriptor templateDescriptor, DataContext editor, List<PsiFile> psiFiles) {
        this.templateDescriptor = templateDescriptor;
        this.editor = editor;
        this.psiFiles = psiFiles;
        this.text = templateDescriptor.getHtmlDisplayName();
        this.tokenizedtext = templateDescriptor.getTokenizedName();
        testMeCreator = new MutiTestMeCreator();
    }

    @NotNull
    @Override
    public String getText() {
        return text;
    }

    @Override
    public Icon getIcon() {
        return null;//Icons.TEST_ME;
    }

    @Override
    public void execute(Project project) {
        for (PsiFile psiFile : psiFiles) {
            testMeCreator.createTest(editor, psiFile, templateDescriptor);
        }
    }

    public String getTokenizedtext() {
        return tokenizedtext;
    }
}
