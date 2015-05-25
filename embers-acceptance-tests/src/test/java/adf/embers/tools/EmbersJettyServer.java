package adf.embers.tools;

import adf.embers.configuration.EmbersConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.sql.DataSource;

/**
 * Created by alex on 25/05/2015.
 */
public class EmbersJettyServer {

    private Server server;

    public void startHttpServer(DataSource dataSource) throws Exception {
        EmbersConfiguration embersConfiguration = new EmbersConfiguration(dataSource);

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(embersConfiguration.getQueryHandler());
        resourceConfig.register(embersConfiguration.getAdminQueryHandler());

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/");
        //setting context path separately works
        handler.setContextPath("/" + EmbersServer.CONTEXT_PATH_ROOT);

        server = new Server(EmbersServer.PORT);
        server.setHandler(handler);
        server.start();
    }

    public void stopHttpServer() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
