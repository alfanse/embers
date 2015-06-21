package adf.embers.tools;

import adf.embers.configuration.EmbersConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;
import javax.sql.DataSource;

public class EmbersJettyServer {

    private final Server server;

    public EmbersJettyServer(int port) {
        this.server = new Server(port);
    }

    public void startHttpServer(DataSource dataSource) throws Exception {
        System.out.println("Starting the Embers Server");
        EmbersConfiguration embersConfiguration = new EmbersConfiguration(dataSource);
        Servlet jerseyServlet = createJerseyServletWithEmbersHandlers(embersConfiguration);

        server.setHandler(createEmbersHandler(jerseyServlet));
        server.start();
    }

    public void stopHttpServer() {
        System.out.println("Stopping the Embers server");
        try {
            server.stop();
        } catch (Exception e) {
            System.err.println("Exception stopping jetty server: "+e.getMessage());
        }
    }

    private Servlet createJerseyServletWithEmbersHandlers(EmbersConfiguration embersConfiguration) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(embersConfiguration.getQueryHandler());
        resourceConfig.register(embersConfiguration.getAdminQueryHandler());
        resourceConfig.register(embersConfiguration.getQueryResultCacheHandler());
        return new ServletContainer(resourceConfig);
    }

    private Handler createEmbersHandler(Servlet embersServlet) {
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(embersServlet), "/");
        //setting context path separately works
        handler.setContextPath("/" + EmbersServer.CONTEXT_PATH_ROOT);

        return handler;
    }
}
