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

/**
 * Created by alex on 25/05/2015.
 */
public class EmbersJettyServer {

    private Server server;

    public void startHttpServer(DataSource dataSource) throws Exception {
        server = new Server(EmbersServer.PORT);
        final EmbersConfiguration embersConfiguration = new EmbersConfiguration(dataSource);
        Servlet jerseyServlet = createJerseyServletWithEmbersHandlers(embersConfiguration);
        server.setHandler(createEmbersHandler(jerseyServlet));
        server.start();
    }

    private Servlet createJerseyServletWithEmbersHandlers(EmbersConfiguration embersConfiguration) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(embersConfiguration.getQueryHandler());
        resourceConfig.register(embersConfiguration.getAdminQueryHandler());
        return new ServletContainer(resourceConfig);
    }

    private Handler createEmbersHandler(Servlet embersServlet) {
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(embersServlet), "/");
        //setting context path separately works
        handler.setContextPath("/" + EmbersServer.CONTEXT_PATH_ROOT);

        return handler;
    }

    public void stopHttpServer() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
