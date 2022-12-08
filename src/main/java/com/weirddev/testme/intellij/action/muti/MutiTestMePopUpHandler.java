package com.weirddev.testme.intellij.action.muti;

import com.intellij.ide.util.EditSourceUtil;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Ref;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBListWithHintProvider;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.ui.popup.HintUpdateSupply;
import com.intellij.usages.UsageView;
import com.intellij.util.Function;
import com.weirddev.testme.intellij.action.muti.popup.MutiTestMeActionCellRenderer;
import com.weirddev.testme.intellij.icon.IconTokensReplacerImpl;
import com.weirddev.testme.intellij.icon.TemplateNameFormatter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class MutiTestMePopUpHandler {
    private static final PsiElementListCellRenderer ourDefaultTargetElementRenderer = new com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.DefaultPsiElementListCellRenderer();
    private final DefaultListCellRenderer myActionElementRenderer = new MutiTestMeActionCellRenderer(new TemplateNameFormatter(), new IconTokensReplacerImpl()); // todo DI

    private static PsiElementListCellRenderer getRenderer(Object value,
                                                          Map<Object, PsiElementListCellRenderer> targetsWithRenderers,
                                                          com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.GotoData gotoData) {
        PsiElementListCellRenderer renderer = targetsWithRenderers.get(value);
        if (renderer == null) {
            renderer = gotoData.getRenderer(value);
        }
        if (renderer != null) {
            return renderer;
        } else {
            return ourDefaultTargetElementRenderer;
        }
    }

    //    @Override
    public boolean startInWriteAction() {
        return false;
    }

    //    @Override
    public void invoke(@NotNull Project project, /*@NotNull*/ DataContext editor, List<PsiFile> files) {


        try {
            com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.GotoData gotoData =
                    getSourceAndTargetElements(editor, files);
            if (gotoData != null) {
                show(project, editor, files, gotoData);
            }
        } catch (IndexNotReadyException e) {
            DumbService.getInstance(project).showDumbModeNotification("Test Generation is not available here during index update");
        }

    }

    @NonNls
    protected abstract String getFeatureUsedKey();

    @Nullable
    protected abstract com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.GotoData getSourceAndTargetElements(DataContext editor, List<PsiFile> file);

    protected void showError(String message) {
        NotificationGroup notificationGroup = new NotificationGroup("testid", NotificationDisplayType.BALLOON, false);
        Notification notification = notificationGroup.createNotification(message, MessageType.ERROR);
        Notifications.Bus.notify(notification);
    }

    private void show(@NotNull final Project project,
            /* @NotNull */DataContext dataContext,
                      @NotNull List<PsiFile> file,
                      @NotNull final com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.GotoData gotoData) {
        final List<com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction> additionalActions = gotoData.additionalActions;

        if (additionalActions.isEmpty()) {
//            HintManager.getInstance().showErrorHint(editor, getNotFoundMessage(project, editor, file));
            showError("not found file");
            return;
        }
        final String title = getChooserTitle(file, gotoData.source);
        final JBListWithHintProvider list = new JBListWithHintProvider(new CollectionListModel<Object>(additionalActions)) {
            @Override
            protected PsiElement getPsiElementForHint(final Object selectedValue) {
                return selectedValue instanceof PsiElement ? (PsiElement) selectedValue : null;
            }
        };

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null)
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction) {
                    return myActionElementRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
                PsiElementListCellRenderer renderer = getRenderer(value, gotoData.renderers, gotoData);
                return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int[] ids = list.getSelectedIndices();
                if (ids == null || ids.length == 0) return;
                List<?> selectedElements = list.getSelectedValuesList();
                for (Object element : selectedElements) {
                    if (element instanceof com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction) {
                        ((com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction) element).execute(CommonDataKeys.PROJECT.getData(dataContext));
                    } else {
                        Navigatable nav = element instanceof Navigatable ? (Navigatable) element : EditSourceUtil.getDescriptor((PsiElement) element);
                        try {
                            if (nav != null && nav.canNavigate()) {
                                navigateToElement(nav);
                            }
                        } catch (IndexNotReadyException e) {
                            DumbService.getInstance(project).showDumbModeNotification("Test Generation is not available while indexing");
                        }
                    }
                }
            }
        };

        final PopupChooserBuilder builder = new PopupChooserBuilder(list);
        builder.setFilteringEnabled(new Function<Object, String>() {
            @Override
            public String fun(Object o) {
                if (o instanceof com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction) {
                    return ((com.weirddev.testme.intellij.ui.popup.TestMePopUpHandler.AdditionalAction) o).getText();
                }
                return getRenderer(o, gotoData.renderers, gotoData).getElementText((PsiElement) o);
            }
        });

        final Ref<UsageView> usageView = new Ref<UsageView>();
        final JBPopup popup = builder.
                setTitle(title).
                setItemChoosenCallback(runnable).
                setMovable(true).
                setCancelCallback(new Computable<Boolean>() {
                    @Override
                    public Boolean compute() {
                        HintUpdateSupply.hideHint(list);
                        return true;
                    }
                }).
                setAdText(getAdText(gotoData.source, 0)).
                createPopup();
        if (gotoData.listUpdaterTask != null) {
            gotoData.listUpdaterTask.init((AbstractPopup) popup, list, usageView);
            ProgressManager.getInstance().run(gotoData.listUpdaterTask);
        }

        if (ApplicationManager.getApplication().isHeadlessEnvironment()) {
            //for UT support - otherwise theres a swing error when popup set relative to fake test editor
            popup.showCenteredInCurrentWindow(project);
        } else {
            popup.showInBestPositionFor(dataContext);
        }
    }

    protected void navigateToElement(Navigatable descriptor) {
        descriptor.navigate(true);
    }


    protected abstract String getChooserTitle(List<PsiFile> file, PsiElement sourceElement);

    @NotNull
    protected abstract String getNotFoundMessage(@NotNull Project project, @NotNull DataContext editor, @NotNull PsiFile file);

    @Nullable
    protected String getAdText(PsiElement source, int length) {
        return null;
    }

//  public interface AdditionalAction {
//    @NotNull
//    String getText();
//
//    Icon getIcon();
//
//    void execute(Project project);
//  }

//  public static class GotoData {
//    @NotNull public final PsiElement source;
//    public final List<AdditionalAction> additionalActions;
//
//    public ListBackgroundUpdaterTask listUpdaterTask; //todo un-used. check relevancy for future features
//    public Map<Object, PsiElementListCellRenderer> renderers = new HashMap<Object, PsiElementListCellRenderer>();//todo un-used. consider using these renders instead of ourDefaultTargetElementRenderer = new DefaultPsiElementListCellRenderer()
//
//    public GotoData(@NotNull PsiElement source, @NotNull List<AdditionalAction> additionalActions) {
//      this.source = source;
//      this.additionalActions = additionalActions;
//    }
//
//    public PsiElementListCellRenderer getRenderer(Object value) {
//      return renderers.get(value);
//    }
//  }
//
//  private static class DefaultPsiElementListCellRenderer extends PsiElementListCellRenderer {
//    @Override
//    public String getElementText(final PsiElement element) {
//      if (element instanceof PsiNamedElement) {
//        String name = ((PsiNamedElement)element).getName();
//        if (name != null) {
//          return name;
//        }
//      }
//      return element.getContainingFile().getName();
//    }
//
//    @Override
//    protected String getContainerText(final PsiElement element, final String name) {
//      if (element instanceof NavigationItem) {
//        final ItemPresentation presentation = ((NavigationItem)element).getPresentation();
//        return presentation != null ? presentation.getLocationString():null;
//      }
//
//      return null;
//    }
//
//    @Override
//    protected int getIconFlags() {
//      return 0;
//    }
//  }
}
