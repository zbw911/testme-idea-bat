package com.weirddev.testme.intellij.action.muti;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
//import com.weirddev.testme.intellij.action.TestMeCreator;
import com.weirddev.testme.intellij.template.TemplateDescriptor;
import com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


/**
 * Date: 10/15/2016
 *
 * @author Yaron Yamin
 */
public class MutiTestMeAdditionalAction implements TestMePopUpHandler.AdditionalAction {

    private final TemplateDescriptor templateDescriptor;
    private final DataContext editor;
    private final PsiFile file;
    private final String text;
    private final MutiTestMeCreator testMeCreator;
    private final String tokenizedtext;

    public MutiTestMeAdditionalAction(TemplateDescriptor templateDescriptor, DataContext editor, PsiFile file) {
        this.templateDescriptor = templateDescriptor;
        this.editor = editor;
        this.file = file;
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
        testMeCreator.createTest(editor, file, templateDescriptor);
    }

    public String getTokenizedtext() {
        return tokenizedtext;
    }
}
