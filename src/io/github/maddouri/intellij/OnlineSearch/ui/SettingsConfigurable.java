package io.github.maddouri.intellij.OnlineSearch.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBScrollPane;
import io.github.maddouri.intellij.OnlineSearch.action.LaunchSearchActionRegistration;
import io.github.maddouri.intellij.OnlineSearch.state.PluginSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/** Plugin configuration in the IDE's "Settings" window. (Settings | Other Settings | OnlineSearch)
 *
 *  @TODO cleanup / refactoring
 */
public class SettingsConfigurable implements Configurable {

    /** The actual GUI panel for the settings
     */
    private class SettingsPanel extends JPanel {

        /** A single (up | down | engine name | engine url | remove) entry in the settings panel
         */
        private class SearchEngineEntry extends JPanel {
            public final JButton    up   = new JButton(AllIcons.Actions.MoveUp);
            public final JButton    down = new JButton(AllIcons.Actions.MoveDown);

            public final JTextField name             = new JTextField("", 1);  // apparently, if I don't add the second argument (any number), the GridBagLayout will mess things up :)
            public final JTextField url              = new JTextField("", 1);
            public final JTextField queryPlaceholder = new JTextField("", 1);

            public final JButton    rem  = new JButton("Remove", AllIcons.Actions.Delete);

            public SearchEngineEntry(final PluginSettings.SearchEngine searchEngine) {
                super();

                /// prepare the layout

                GridBagLayout layout = new GridBagLayout();
                setLayout(layout);

                /// add the elements + action listeners

                up.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // https://stackoverflow.com/a/22953344/865719
                        SearchEngineEntry entry  = SearchEngineEntry.this;
                        Container         parent = entry.getParent();
                        int               zorder = parent.getComponentZOrder(entry) - 1;
                        if (zorder >= 0) {
                            parent.setComponentZOrder(entry, zorder);
                            SettingsConfigurable.this.setModified(true);
                        }
                    }
                });
                down.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // https://stackoverflow.com/a/22953344/865719
                        SearchEngineEntry entry  = SearchEngineEntry.this;
                        Container         parent = entry.getParent();
                        int               zorder = parent.getComponentZOrder(entry) + 1;
                        if (zorder < parent.getComponentCount()) {
                            parent.setComponentZOrder(entry, zorder);
                            SettingsConfigurable.this.setModified(true);
                        }
                    }
                });
                name.addKeyListener(new KeyListener() {
                    @Override public void keyTyped(KeyEvent e)    { SettingsConfigurable.this.setModified(true); }
                    @Override public void keyPressed(KeyEvent e)  { SettingsConfigurable.this.setModified(true); }
                    @Override public void keyReleased(KeyEvent e) { SettingsConfigurable.this.setModified(true); }
                });
                url.addKeyListener(new KeyListener() {
                    @Override public void keyTyped(KeyEvent e)    { SettingsConfigurable.this.setModified(true); }
                    @Override public void keyPressed(KeyEvent e)  { SettingsConfigurable.this.setModified(true); }
                    @Override public void keyReleased(KeyEvent e) { SettingsConfigurable.this.setModified(true); }
                });
                queryPlaceholder.addKeyListener(new KeyListener() {
                    @Override public void keyTyped(KeyEvent e)    { SettingsConfigurable.this.setModified(true); }
                    @Override public void keyPressed(KeyEvent e)  { SettingsConfigurable.this.setModified(true); }
                    @Override public void keyReleased(KeyEvent e) { SettingsConfigurable.this.setModified(true); }
                });
                rem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SettingsPanel.this.removeSearchEngineEntry(SearchEngineEntry.this);
                        SettingsConfigurable.this.setModified(true);
                    }
                });

                /// configure the layout

                //name.setMaximumSize(new Dimension(Integer.MAX_VALUE, name.getPreferredSize().height));
                //url.setMaximumSize(new Dimension(Integer.MAX_VALUE, url.getPreferredSize().height));

                GridBagConstraints ctr = new GridBagConstraints(0, 0,
                                                                1, 1,
                                                                1.0, 1.0,
                                                                GridBagConstraints.FIRST_LINE_START,
                                                                GridBagConstraints.NONE,
                                                                new Insets(0, 0, 0, 0),
                                                                0, 0);

                ctr.insets = new Insets(0,0,0,0);
                ctr.gridy = 0;
                ctr.gridwidth = 1;
                ctr.fill = GridBagConstraints.BOTH;

                int gridx = 0;

                ctr.gridx = gridx++;
                ctr.weightx = 0.05;
                add(up, ctr);

                ctr.gridx = gridx++;
                ctr.weightx = 0.05;
                add(down, ctr);

                ctr.gridx = gridx++;
                ctr.weightx = 1.0;
                add(name, ctr);

                ctr.gridx = gridx++;
                ctr.weightx = 2.0;
                add(url, ctr);

                ctr.gridx = gridx++;
                ctr.weightx = 0.5;
                add(queryPlaceholder, ctr);

                ctr.gridx = gridx++;
                ctr.weightx = 0.2;
                add(rem, ctr);

                this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));

                /// init the text fields

                if (searchEngine != null) {  // loading from the config file
                    name.setText(searchEngine.name);
                    url.setText(searchEngine.url);
                    if (searchEngine.queryPlaceholder == null || searchEngine.queryPlaceholder.equals("")) {  // compatibility with pre-1.1.1
                        queryPlaceholder.setText(PluginSettings.DEFAULT_QUERY_PLACEHOLDER);
                    } else {
                        queryPlaceholder.setText(searchEngine.queryPlaceholder);
                    }
                } else {  // "add search engine" button pressed
                    name.setText("");
                    url.setText("");
                    queryPlaceholder.setText("");//PluginSettings.DEFAULT_QUERY_PLACEHOLDER);
                }

            }

            public void cleanup() {

                for (ActionListener l : up.getActionListeners()) {
                    up.removeActionListener(l);
                }

                for (ActionListener l : down.getActionListeners()) {
                    down.removeActionListener(l);
                }

                for (KeyListener l : name.getKeyListeners()) {
                    name.removeKeyListener(l);
                }

                for (KeyListener l : url.getKeyListeners()) {
                    url.removeKeyListener(l);
                }

                for (ActionListener l : rem.getActionListeners()) {
                    rem.removeActionListener(l);
                }

                this.removeAll();

                SettingsConfigurable.this.setModified(true);
            }
        }

        /** Container for the list of {@link io.github.maddouri.intellij.OnlineSearch.ui.SettingsConfigurable.SettingsPanel.SearchEngineEntry}'s
         */
        public class SearchEnginePanel extends JPanel {

            public void cleanup() {

                for (Component c : this.getComponents()) {
                    if (c.getClass() == SearchEngineEntry.class) {
                        ((SearchEngineEntry) c).cleanup();
                    }
                }

                this.removeAll();

                SettingsConfigurable.this.setModified(true);
            }

            public void addSearchEngineEntry(final PluginSettings.SearchEngine searchEngine) {
                add(new SearchEngineEntry(searchEngine));
                SettingsConfigurable.this.setModified(true);
            }

            public void removeSearchEngineEntry(final SearchEngineEntry entry) {
                entry.cleanup();
                searchEnginePanel.remove(entry);
                SettingsConfigurable.this.setModified(true);
            }

            public void addSearchEngines(final ArrayList<PluginSettings.SearchEngine> searchEngines) {
                for (PluginSettings.SearchEngine engine : searchEngines) {
                    addSearchEngineEntry(engine);
                }
                SettingsConfigurable.this.setModified(true);
            }

            public void removeAllSearchEngines() {
                cleanup();
                SettingsConfigurable.this.setModified(true);
            }

            public ArrayList<PluginSettings.SearchEngine> getSearchEngines() {
                ArrayList<PluginSettings.SearchEngine> searchEngines = new ArrayList<>();

                for (final Component component : searchEnginePanel.getComponents()) {
                    final SearchEngineEntry entry = (SettingsPanel.SearchEngineEntry) component;

                    final String name             = entry.name.getText();
                    final String url              = entry.url.getText();
                    final String queryPlaceholder = entry.queryPlaceholder.getText();

                    if (   name             != null && !name.equals("")
                        && url              != null && !url.equals("")
                        && queryPlaceholder != null && !queryPlaceholder.equals("")) {
                        searchEngines.add(new PluginSettings.SearchEngine(name, url, queryPlaceholder));
                    }

                }

                return searchEngines;
            }
        }


        // https://stackoverflow.com/a/6635733/865719
        private JLabel titleLabel = new JLabel(
            "<html>" +
                "<p>Add/Remove Search Engines</p>" +
                "<ul>" +
                    "<li>Use \"<b>" + PluginSettings.DEFAULT_QUERY_PLACEHOLDER + "</b>\" or <b>any other string</b> as the query placeholder</li>" +
                    "<li>Use one underscore \"<b>_</b>\" before the <em>\"shortcut letter\"</em> in the engine name</li>" +
                "</ul>" +
            "</html>");
        private SearchEnginePanel searchEnginePanel     = new SearchEnginePanel();
        private JButton            addSearchEngineButton = new JButton("Add Search Engine", AllIcons.General.Add);

        public SettingsPanel(final ArrayList<PluginSettings.SearchEngine> searchEngines) {
            super();
            setLayout(new BorderLayout());

            // title
            add(titleLabel, BorderLayout.PAGE_START);

            // search engines
            searchEnginePanel.setLayout(new BoxLayout(searchEnginePanel, BoxLayout.Y_AXIS));
            addSearchEngines(searchEngines);
            JBScrollPane sp = new JBScrollPane(searchEnginePanel);
            sp.setPreferredSize(new Dimension(512, 32));
            add(sp, BorderLayout.CENTER);

            // add search engine button
            addSearchEngineButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addSearchEngineEntry(null);
                }
            });
            add(addSearchEngineButton, BorderLayout.PAGE_END);

            this.revalidate();
        }

        public void cleanup() {

            for (ActionListener l : addSearchEngineButton.getActionListeners()) {
                addSearchEngineButton.removeActionListener(l);
            }

            searchEnginePanel.cleanup();

            this.removeAll();
        }

        public void addSearchEngineEntry(final PluginSettings.SearchEngine searchEngine) {
            searchEnginePanel.addSearchEngineEntry(searchEngine);
        }

        public void removeSearchEngineEntry(final SearchEngineEntry entry) {
            searchEnginePanel.removeSearchEngineEntry(entry);
            SettingsConfigurable.this.setModified(true);
        }

        public void addSearchEngines(final ArrayList<PluginSettings.SearchEngine> searchEngines) {
            searchEnginePanel.addSearchEngines(searchEngines);
        }

        public void removeAllSearchEngines() {
            searchEnginePanel.removeAllSearchEngines();
            SettingsConfigurable.this.setModified(true);
        }

        public ArrayList<PluginSettings.SearchEngine> getSearchEngines() {
            return searchEnginePanel.getSearchEngines();
        }
    }

    /// SettingsConfigurable

    private SettingsPanel thePanel = null;
    private boolean       modified = false;

    /// Configurable

    @Nls
    @Override
    public String getDisplayName() {
        return "OnlineSearch";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "Add/remove search engines from OnlineSearch's menu";
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        PluginSettings settings = PluginSettings.getInstance();

        thePanel = new SettingsPanel(settings.searchEngines);
        modified = false;

        return thePanel;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public void setModified(final boolean b) {
        modified = b;
    }

    @Override
    public void apply() throws ConfigurationException {
        PluginSettings settings = PluginSettings.getInstance();

        settings.searchEngines.clear();
        settings.searchEngines = thePanel.getSearchEngines();

        LaunchSearchActionRegistration.reloadComponent();  // @TODO there should a "better" way of reloading the settings...

        modified = false;
    }

    @Override
    public void reset() {
        thePanel.removeAllSearchEngines();
        thePanel.addSearchEngines(PluginSettings.getInstance().searchEngines);

        modified = false;
    }

    @Override
    public void disposeUIResources() {
        thePanel.cleanup();
        thePanel = null;
    }

}
