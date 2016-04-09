package io.github.maddouri.intellij.OnlineSearch.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

/**
 * Creates a popup showing the list of available search engines to choose from.
 * Can be assigned a keyboard shortcut. (Settings | Keymap | OnlineSearch)
 */
public class ShowSearchEnginesAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup actionGroup = (DefaultActionGroup) am.getAction(LaunchSearchActionRegistration.COMPONENT_GROUP);

        // https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Popups
        // https://github.com/linux-china/idea-string-manipulation/blob/master/src/main/java/osmedile/intellij/stringmanip/PopupChoiceAction.java#L23
        JBPopupFactory
                .getInstance()
                .createActionGroupPopup("OnlineSearch",
                                        actionGroup,
                                        e.getDataContext(),
                                        JBPopupFactory.ActionSelectionAid.NUMBERING,
                                        false)
                .showInBestPositionFor(e.getDataContext());
    }
}
