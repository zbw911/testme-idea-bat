package com.weirddev.testme.intellij.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import java.util.Arrays;
import java.util.List;
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
        Notification notification = notificationGroup.createNotification("测试通知", MessageType.ERROR);
        Notifications.Bus.notify(notification);

        VirtualFile[] virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        //选中节点
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(e.getDataContext());
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        List<PsiFile> psiFiles;
        if (psiElement != null) {
            //Project 中先择了 包 或 单个文件
            if (psiElement instanceof PsiDirectory) {
//               todo: 这果有 bug ,   可能是还有子文件夹,应继续递归
                psiFiles = Arrays.stream(psiElement.getChildren())
                        .filter(x -> x instanceof PsiFile)
                        .map(x -> (PsiFile) x)
                        .collect(Collectors.toList());
            } else if (psiElement instanceof PsiClass) {
                PsiFile containingFile = psiElement.getContainingFile();
                psiFiles = Arrays.asList(containingFile);
            } else {
                throw new RuntimeException("no file select psiElement");
            }

        } else if (psiFile != null) {
            psiFiles = Arrays.asList(psiFile);
        } else if (virtualFiles.length > 1) {
            // todo ,  这个用来实现一个多选，但是还有Bug，因为 VFS 可能是选了文件夹,应继续递归
            psiFiles = Arrays.stream(virtualFiles)
                    .map(x -> PsiManager.getInstance(project).findFile(x))
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("no file select");
        }
        com.weirddev.testme.intellij.action.muti.TestMeActionHandler testMeActionHandler = new com.weirddev.testme.intellij.action.muti.TestMeActionHandler();
        testMeActionHandler.invoke(project, e.getDataContext(), psiFiles);
    }
}
