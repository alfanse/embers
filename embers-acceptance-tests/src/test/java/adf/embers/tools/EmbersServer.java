package adf.embers.tools;

import adf.embers.configuration.EmbersConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.rules.ExternalResource;

/* investigate http://crunchify.com/how-to-start-embedded-http-jersey-server-during-java-application-startup/
to replace jetty with jersey server */

public class EmbersServer extends ExternalResource {

    public static final int PORT = 8001;
    public static final String CONTEXT_PATH_ROOT = "embers";

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
        embersDatabase.createTableQueriesStatistics();
    }

    public void startHttpServer() throws Exception {
        EmbersConfiguration embersConfiguration = new EmbersConfiguration(embersDatabase.getDataSource());

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(embersConfiguration.getQueryHandler());
        resourceConfig.register(embersConfiguration.getAdminQueryHandler());

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/");
        //setting context path separately works
        handler.setContextPath("/" + CONTEXT_PATH_ROOT);

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

    /**
     * starting from http:// including /embers
     */
    public String getFullContextPath() {
        return "http://localhost:" + PORT + "/" + CONTEXT_PATH_ROOT;
    }

}
