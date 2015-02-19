package adf.embers.tools;

import adf.embers.configuration.EmbersConfiguration;
import adf.embers.query.QueryHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.rules.ExternalResource;

public class EmbersServer extends ExternalResource {

    private static final int PORT = 8001;

    private EmbersDatabase embersDatabase;
    private Server server;

    @Override
    protected void before() throws Throwable {
        startDatabase();
        startHttpServer();
    }

    @Override
    protected void after() {
        stopHttpServer();
        embersDatabase.shutdownInMemoryDatabase();
    }

    public void startDatabase() throws Exception {
        embersDatabase = new EmbersDatabase(EmbersDatabase.JDBC_URL);
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueries();
    }

    public void startHttpServer() throws Exception {
        EmbersConfiguration embersConfiguration = new EmbersConfiguration(embersDatabase.getDataSource());

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(embersConfiguration.getQueryHandler());

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/");

        server = new Server(PORT);
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

    public EmbersDatabase getEmbersDatabase() {
        return embersDatabase;
    }

    public String getContextPath() {
        return "http://localhost:"+ PORT +"/" + QueryHandler.PATH;
    }
}
