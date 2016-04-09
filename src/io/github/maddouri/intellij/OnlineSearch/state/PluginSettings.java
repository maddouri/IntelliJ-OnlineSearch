package io.github.maddouri.intellij.OnlineSearch.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

/** The plugin's settings. (mainly the list of search engines)
 *  Gets saved for retrieval when the IDE is started.
 *
 *  @link http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html
 *  @link https://github.com/dubreuia/intellij-plugin-save-actions/blob/master/src/com/dubreuia/model/Storage.java#L11
 */
@State(
    name = "PluginSettings",
    storages = {
        @com.intellij.openapi.components.Storage(
            file = StoragePathMacros.APP_CONFIG + "/OnlineSearch_v1_PluginSettings.xml"
        )
    }
)
public class PluginSettings implements PersistentStateComponent<PluginSettings> {

    public static class SearchEngine implements Serializable {
        public String name = "";
        public String url  = "";

        public SearchEngine() {
        }

        public SearchEngine(String name, String url) {
            this.name = name;
            this.url  = url;
        }
    }

    ///
    public static final String SEARCH_QUERY_PLACEHOLDER = "#";

    @com.intellij.util.xmlb.annotations.Transient  // exclude from state
    public static final ArrayList<SearchEngine> DEFAULT_SEARCH_ENGINES = new ArrayList<SearchEngine>(){{  // https://stackoverflow.com/a/1958961/865719
        add(new SearchEngine("_Google"       , "https://www.google.com/search?q="                    + SEARCH_QUERY_PLACEHOLDER));
        add(new SearchEngine("Git_hub"       , "https://github.com/search?q="                        + SEARCH_QUERY_PLACEHOLDER));
        add(new SearchEngine("_SearchCode"   , "https://searchcode.com/?q="                          + SEARCH_QUERY_PLACEHOLDER));
        add(new SearchEngine("G_repCode"     , "http://grepcode.com/search/?query="                  + SEARCH_QUERY_PLACEHOLDER));  // not https :(
        add(new SearchEngine("_CppReference" , "http://en.cppreference.com/mwiki/index.php?search="  + SEARCH_QUERY_PLACEHOLDER));  // not https :(
        add(new SearchEngine("C_Make"        , "https://cmake.org/cmake/help/latest/search.html?q="  + SEARCH_QUERY_PLACEHOLDER));
    }};
    public ArrayList<SearchEngine> searchEngines = new ArrayList<>();

    public PluginSettings() {
        searchEngines = new ArrayList<>(DEFAULT_SEARCH_ENGINES);
    }

    public static PluginSettings getInstance() {
        return ServiceManager.getService(PluginSettings.class);
    }

    /// PersistentStateComponent

    @Nullable
    @Override
    public PluginSettings getState() {
        return this;
    }

    @Override
    public void loadState(PluginSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
