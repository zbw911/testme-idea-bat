package com.weirddev.testme.intellij.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author zhangbaowei
 * @description:
 * @date 2022-12-06 12:22
 */
public class CreateAllTest extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        NotificationGroup notificationGroup = new NotificationGroup("testid", NotificationDisplayType.BALLOON, false);
        /**
         * content :  通知内容
         * type  ：通知的类型，warning,info,error
         */
        Notification notification = notificationGroup.createNotification("测试通知", MessageType.ERROR);
        Notifications.Bus.notify(notification);
        String s = "";

        VirtualFile data = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        VirtualFile[] data1 = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        PsiElement data2 = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        PsiFile data3 = CommonDataKeys.PSI_FILE.getData(e.getDataContext());
        String collect = Arrays.stream(data.getChildren())
                .map(x -> x.getName())
                .collect(Collectors.joining(","));

//        PsiClass containingClass = CreateTestMeAction.getContainingClass(data2);

        com.weirddev.testme.intellij.action.muti.TestMeActionHandler testMeActionHandler = new com.weirddev.testme.intellij.action.muti.TestMeActionHandler();
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
        testMeActionHandler.invoke(project, editor, data3);
    }
}
