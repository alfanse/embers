package adf.embers.tools;

import adf.embers.configuration.EmbersHandlerConfiguration;
import adf.embers.configuration.EmbersProcessorConfiguration;
import adf.embers.configuration.EmbersRepositoryConfiguration;

import jakarta.servlet.Servlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.inject.hk2.Hk2InjectionManagerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmbersJettyServer {

    private final Server server;

    public EmbersJettyServer(int port) {
        this.server = new Server(port);
    }

    public void startHttpServer(DataSource dataSource) throws Exception {
        System.out.println("Starting the Embers Server on port: " + server.getURI());
        
        // Disable Weld logging to avoid unnecessary warnings
        Logger.getLogger("org.jboss.weld").setLevel(Level.SEVERE);
        
        // Set HK2 as the injection manager
        System.setProperty("jersey.config.server.disableAutoDiscovery", "true");
        System.setProperty("jersey.config.server.disableMetainfServicesLookup", "true");
        System.setProperty("jersey.config.server.disableMoxyJson", "true");
        
        try {
            // Create HK2 service locator
            ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator("EmbersServiceLocator");
            
            // Manually register services
            EmbersRepositoryConfiguration embersRepositoryConfiguration = new EmbersRepositoryConfiguration(dataSource);
            ServiceLocatorUtilities.addOneConstant(locator, embersRepositoryConfiguration);
            
            EmbersProcessorConfiguration embersProcessorConfiguration = new EmbersProcessorConfiguration(embersRepositoryConfiguration);
            ServiceLocatorUtilities.addOneConstant(locator, embersProcessorConfiguration);
            
            EmbersHandlerConfiguration embersConfiguration = new EmbersHandlerConfiguration(embersProcessorConfiguration);
            ServiceLocatorUtilities.addOneConstant(locator, embersConfiguration);
            
            // Create Jersey resource config with HK2
            ResourceConfig resourceConfig = new ResourceConfig();
            resourceConfig.property("jersey.config.server.provider.classnames", 
                "org.glassfish.jersey.media.multipart.MultiPartFeature");
                
            // Register resources
            resourceConfig.register(embersConfiguration.getQueryHandler());
            resourceConfig.register(embersConfiguration.getAdminQueryHandler());
            resourceConfig.register(embersConfiguration.getQueryResultCacheHandler());
            
            // Create servlet container with HK2
            ServletContainer servletContainer = new ServletContainer(resourceConfig);
            
            // Set up the handler
            ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            handler.setContextPath("/" + EmbersServer.CONTEXT_PATH_ROOT);
            ServletHolder servletHolder = new ServletHolder("jersey-servlet", servletContainer);
            handler.addServlet(servletHolder, "/*");
            
            System.out.println("Created handler: " + handler);
            server.setHandler(handler);
            
            System.out.println("Starting Jetty server...");
            server.start();
            System.out.println("Started the Embers Server at: http://localhost:" + server.getURI().getPort() + "/" + EmbersServer.CONTEXT_PATH_ROOT);
        } catch (Exception e) {
            System.err.println("Error starting Embers server: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void stopHttpServer() {
        System.out.println("Stopping the Embers server");
        try {
            server.stop();
            System.out.println("Stopped the Embers server");
        } catch (Exception e) {
            System.err.println("Exception stopping jetty server: " + e.getMessage() + "/n");
        } finally {
            LifeCycle.stop(server);
        }
    }

    private Servlet createJerseyServletWithEmbersHandlers(EmbersHandlerConfiguration embersConfiguration) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(embersConfiguration.getQueryHandler());
        resourceConfig.register(embersConfiguration.getAdminQueryHandler());
        resourceConfig.register(embersConfiguration.getQueryResultCacheHandler());
        return new ServletContainer(resourceConfig);
    }

    private Handler createEmbersHandler(Servlet embersServlet) {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/" + EmbersServer.CONTEXT_PATH_ROOT);
        
        // Add the servlet
        ServletHolder servletHolder = new ServletHolder("embers", embersServlet);
        handler.addServlet(servletHolder, "/*");
        
        return handler;
    }
}
