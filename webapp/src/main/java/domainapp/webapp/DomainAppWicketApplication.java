package domainapp.webapp;

import java.io.InputStream;

import org.apache.isis.viewer.wicket.viewer.IsisWicketApplication;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.google.inject.util.Providers;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider;
import domainapp.modules.base.service.SessionStoreFactory;
import domainapp.webapp.service.WicketSessionStore;

/**
 * As specified in <tt>web.xml</tt>.
 * 
 * <p>
 * See:
 * <pre>
 * &lt;filter>
 *   &lt;filter-name>wicket&lt;/filter-name>
 *    &lt;filter-class>org.apache.wicket.protocol.http.WicketFilter&lt;/filter-class>
 *    &lt;init-param>
 *      &lt;param-name>applicationClassName&lt;/param-name>
 *      &lt;param-value>DemoApplication&lt;/param-value>
 *    &lt;/init-param>
 * &lt;/filter>
 * </pre>
 * 
 */
public class DomainAppWicketApplication extends IsisWicketApplication {

    private static final long serialVersionUID = 1L;

    @Override
    protected void init() {
        super.init();

        IBootstrapSettings settings = Bootstrap.getSettings();
        settings.setThemeProvider(new BootswatchThemeProvider(BootswatchTheme.Flatly));
        
        SessionStoreFactory.INSTANCE.registerSessionStore(new WicketSessionStore());
    }

    private static final String APP_NAME = "Statements Manager";

    @Override
    protected Module newIsisWicketModule() {
        final Module isisDefaults = super.newIsisWicketModule();
        
        final Module overrides = new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("applicationName")).toInstance(APP_NAME);
                bind(String.class).annotatedWith(Names.named("applicationCss")).toInstance("css/application.css");
                bind(String.class).annotatedWith(Names.named("applicationJs")).toInstance("scripts/application.js");
                bind(String.class).annotatedWith(Names.named("welcomeMessage")).toInstance(readLines(getClass(), "welcome.html", APP_NAME));
                bind(String.class).annotatedWith(Names.named("aboutMessage")).toInstance(APP_NAME);
                bind(InputStream.class).annotatedWith(Names.named("metaInfManifest")).toProvider(
                        Providers.of(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF")));
            }
        };

        return Modules.override(isisDefaults).with(overrides);
    }

}
