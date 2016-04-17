package io.github.maddouri.intellij.OnlineSearch.action;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import io.github.maddouri.intellij.OnlineSearch.state.PluginSettings;

import java.io.IOException;
import java.net.URLEncoder;

/** Uses the provided {@link io.github.maddouri.intellij.OnlineSearch.action.LaunchSearchAction#searchEngine}
 *  to search for the selected code.
 *
 *  Launches the web browser.
 *
 *  This action is registered by {@link io.github.maddouri.intellij.OnlineSearch.action.LaunchSearchActionRegistration}
 */
public class LaunchSearchAction extends AnAction {

    /** The search engine
     */
    public final PluginSettings.SearchEngine searchEngine;

    public LaunchSearchAction(final PluginSettings.SearchEngine searchEngine) {
        super(searchEngine.name);
        this.searchEngine = searchEngine;
    }

    @Override
    public void update(final AnActionEvent e) {
        // http://www.jetbrains.org/intellij/sdk/docs/tutorials/editor_basics/working_with_text.html

        // Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor   = e.getData(CommonDataKeys.EDITOR);

        // Set visibility only in case of an existing project, an editor and a selection
        e.getPresentation().setVisible(project != null
                                       && editor != null
                                       && editor.getSelectionModel().hasSelection(true));
        //e.getPresentation().setVisible(true);
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final String selectedText = getText(e);
        launchSearch(selectedText);
    }

    private String getText(final AnActionEvent e) {

        // @TODO add ability to get data from list-type views -- e.g. 1:Project, 7:Structure, Favorites, 6:TODO...
        try {
            return getText_fromEditor(e);
        } catch (Exception | AssertionError ex) {
            Messages.showMessageDialog("Error in \"" + e.getPlace() + "\"\n" + ex.toString(),
                                       "Error When Launching the Browser",
                                       Messages.getErrorIcon());
            System.err.println("Error When Launching the Browser:\n" +
                               "\tError in \"" + e.getPlace() + "\"\n\t" + ex.toString());
            return null;
        }
    }

    private String getText_fromEditor(final AnActionEvent e) {
        // http://www.jetbrains.org/intellij/sdk/docs/tutorials/editor_basics/working_with_text.html

        // get the editor
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        // access selection
        return editor.getSelectionModel().getSelectedText(true);
    }

    private void launchSearch(final String query) {

        if (query == null) {
            Messages.showMessageDialog("query == null",
                                       "Error When Launching the Browser",
                                       Messages.getErrorIcon());
            System.err.println("Error When Launching the Browser: query == null");
            return;
        }

        try {
            final String encodedQuery = URLEncoder.encode(query, "UTF-8");
            final String uriString    = searchEngine.url.replace(searchEngine.queryPlaceholder,
                                                                 encodedQuery);

            BrowserLauncher.getInstance().open(uriString);

        } catch (IOException ex) {
            Messages.showMessageDialog(ex.toString(),
                                       "Error When Launching the Browser",
                                       Messages.getErrorIcon());
            System.err.println("Error When Launching the Browser: " + ex.toString());
        }
    }
}
