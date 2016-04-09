package io.github.maddouri.intellij.OnlineSearch.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import io.github.maddouri.intellij.OnlineSearch.state.PluginSettings;
import org.jetbrains.annotations.NotNull;

/** Registers a new action with each search engine.
 */
public class LaunchSearchActionRegistration implements ApplicationComponent {

    public static final String COMPONENT_GROUP = "OnlineSearch.EditorPopupMenu.LaunchSearchSubmenu";  // @see plugin.xml/actions
    public static final String COMPONENT_NAME  = "OnlineSearchComponent";

    public static void reloadComponent() {
        LaunchSearchActionRegistration component = (LaunchSearchActionRegistration) ApplicationManager.getApplication().getComponent(COMPONENT_NAME);
        component.disposeComponent();
        component.initComponent();
    }

    private static String getActionId(LaunchSearchAction a) {
        return "LaunchSearchAction" + a.name;
    }

    // Returns the component name (any unique string value).
    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    // If you register the MyPluginRegistration class in the <application-components> section of
    // the plugin.xml file, this method is called on IDEA start-up.
    @Override
    public void initComponent() {
        PluginSettings settings = PluginSettings.getInstance();
        ActionManager am = ActionManager.getInstance();

        // Gets an instance of the WindowMenu action group.
        DefaultActionGroup menuManager = (DefaultActionGroup) am.getAction(COMPONENT_GROUP); //(IdeActions.GROUP_EDITOR_POPUP); //"EditorPopupMenu");
        //menuManager.addSeparator();

        for (PluginSettings.SearchEngine engine : settings.searchEngines) {
            LaunchSearchAction action = new LaunchSearchAction(engine.name, engine.url);

            // Passes an instance of your custom TextBoxes class to the registerAction method of the ActionManager class.
            am.registerAction(getActionId(action), action);

            // Adds a separator and a new menu command to the WindowMenu group on the main menu.
            menuManager.add(action);  //, Constraints.LAST);
        }

        menuManager.addSeparator();
    }

    // Disposes system resources.
    @Override
    public void disposeComponent() {
        ActionManager am = ActionManager.getInstance();

        // Gets an instance of the WindowMenu action group.
        DefaultActionGroup menuManager = (DefaultActionGroup) am.getAction(COMPONENT_GROUP); //(IdeActions.GROUP_EDITOR_POPUP); //"EditorPopupMenu");

        for (AnAction a : menuManager.getChildActionsOrStubs()) {
            if (a.getClass() == LaunchSearchAction.class) {
                am.unregisterAction(getActionId((LaunchSearchAction) a));
            }
        }

        // Adds a separator and a new menu command to the WindowMenu group on the main menu.
        menuManager.removeAll();
    }


}
