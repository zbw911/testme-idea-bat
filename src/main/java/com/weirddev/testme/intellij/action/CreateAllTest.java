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
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.weirddev.testme.intellij.action.muti.MutiTestMeActionHandler;

import java.util.ArrayList;
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


        VirtualFile[] virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        //选中节点
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        List<VirtualFile> fileList = findFile(virtualFiles);
        List<PsiFile> psiFiles;
        psiFiles = fileList
                .stream()
                .map(x -> PsiManager.getInstance(project).findFile(x))
                .collect(Collectors.toList());

        if (psiFiles.size() == 0) {
            NotificationGroup notificationGroup = new NotificationGroup("testid", NotificationDisplayType.BALLOON, false);
            Notification notification = notificationGroup.createNotification("未选中文件", MessageType.INFO);
            Notifications.Bus.notify(notification);
        }
        MutiTestMeActionHandler testMeActionHandler = new MutiTestMeActionHandler();
        testMeActionHandler.invoke(project, e.getDataContext(), psiFiles);
    }

    private List<VirtualFile> findFile(VirtualFile virtualFile) {
        List<VirtualFile> list = new ArrayList<>();
        if (virtualFile instanceof VirtualDirectoryImpl) {
            VirtualFile[] children = virtualFile.getChildren();
            list.addAll(findFile(children));
        } else if (virtualFile instanceof VirtualFileImpl) {
            list.add(virtualFile);
        } else {

            var x = virtualFile;
        }

        return list;
    }

    private List<VirtualFile> findFile(VirtualFile[] virtualFiles) {
        List<VirtualFile> list = new ArrayList<>();

        for (VirtualFile virtualFile : virtualFiles) {
            list.addAll(findFile(virtualFile));
        }

        return list;
    }
}
