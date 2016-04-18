package io.github.maddouri.intellij.OnlineSearch.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
            /* @TODO use `value` instead of `file` to get rid of the "deprecated" warning */
            file = StoragePathMacros.APP_CONFIG + "/OnlineSearch_v1_PluginSettings.xml"
        )
    }
)
public class PluginSettings implements PersistentStateComponent<PluginSettings> {

    ///
    public static final String DEFAULT_QUERY_PLACEHOLDER = "#";

    public static class SearchEngine implements Serializable {

        public String name             = "";
        public String url              = "";
        public String queryPlaceholder = "";  /* setting this to "" will cause the configuration GUI to set DEFAULT_QUERY_PLACEHOLDER
                                               * as the placeholder value (cf. {@link SettingsConfigurable.SettingsPanel.SearchEngineEntry#SearchEngineEntry(final PluginSettings.SearchEngine searchEngine)}),
                                               * which will ultimately cause the addition of the `<option name="queryPlaceholder" />` tag under the pre-1.1.1 `<SearchEngine>` entries in the config file.
                                               * NB: the config file (and, therefore, the pre-1.1.1 entries) will only be updated if the search engine list has changed (e.g. adding/removing engines and/or editing the existing ones)
                                               */

        public SearchEngine() {
        }

        public SearchEngine(String name, String url, String queryPlaceholder) {
            this.name             = name;
            this.url              = url;
            this.queryPlaceholder = queryPlaceholder;
        }

        public String generateSearchUri(final String query) throws UnsupportedEncodingException {
            final String encodedQuery = URLEncoder.encode(query, "UTF-8");

            if (queryPlaceholder == null || queryPlaceholder.equals("")) {  // fix for the 1.1.1 bug causing a ill-formed URI to be generated
                return url.replace(DEFAULT_QUERY_PLACEHOLDER, encodedQuery);
            } else {
                return url.replace(queryPlaceholder, encodedQuery);
            }
        }
    }

    @com.intellij.util.xmlb.annotations.Transient  // exclude from state
    public static final ArrayList<SearchEngine> DEFAULT_SEARCH_ENGINES = new ArrayList<SearchEngine>(){{  // https://stackoverflow.com/a/1958961/865719
        add(new SearchEngine("_Google"       , "https://www.google.com/search?q="                    + DEFAULT_QUERY_PLACEHOLDER, DEFAULT_QUERY_PLACEHOLDER));
        add(new SearchEngine("Git_hub"       , "https://github.com/search?q="                        + DEFAULT_QUERY_PLACEHOLDER, DEFAULT_QUERY_PLACEHOLDER));
        add(new SearchEngine("_SearchCode"   , "https://searchcode.com/?q="                          + DEFAULT_QUERY_PLACEHOLDER, DEFAULT_QUERY_PLACEHOLDER));
        add(new SearchEngine("G_repCode"     , "http://grepcode.com/search/?query="                  + DEFAULT_QUERY_PLACEHOLDER, DEFAULT_QUERY_PLACEHOLDER));  // not https :(
        add(new SearchEngine("_CppReference" , "http://en.cppreference.com/mwiki/index.php?search="  + DEFAULT_QUERY_PLACEHOLDER, DEFAULT_QUERY_PLACEHOLDER));  // not https :(
        add(new SearchEngine("C_Make"        , "https://cmake.org/cmake/help/latest/search.html?q="  + DEFAULT_QUERY_PLACEHOLDER, DEFAULT_QUERY_PLACEHOLDER));
    }};

    /** The list of search engines
     *
     *  This will be saved in the config file.
     */
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
