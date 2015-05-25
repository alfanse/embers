package adf.embers.tools;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryHandler;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;

/* investigate http://crunchify.com/how-to-start-embedded-http-jersey-server-during-java-application-startup/
to replace jetty with jersey server */

public class EmbersServer extends ExternalResource {

    public static final int PORT = 8001;
    public static final String CONTEXT_PATH_ROOT = "embers";

    private EmbersDatabase embersDatabase;
    private EmbersJettyServer embersJettyServer;

    @Override
    protected void before() throws Throwable {
        startDatabase();
        startJettyServer(embersDatabase.getDataSource());
    }

    @Override
    protected void after() {
        embersJettyServer.stopHttpServer();
        embersDatabase.shutdownInMemoryDatabase();
    }

    private void startDatabase() throws Exception {
        embersDatabase = new EmbersDatabase(EmbersDatabase.JDBC_URL);
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueries();
        embersDatabase.createTableQueriesStatistics();
    }

    private void startJettyServer(DataSource dataSource) throws Exception {
        embersJettyServer = new EmbersJettyServer();
        embersJettyServer.startHttpServer(dataSource);
    }

    public EmbersDatabase getEmbersDatabase() {
        return embersDatabase;
    }

    public String embersQueryPath() {
        return getEmbersContextPath() + "/" + QueryHandler.PATH;
    }

    public String embersAdminPath() {
        return getEmbersContextPath() + AdminQueryHandler.PATH;
    }

    /**
     * starting from http:// up to /embers
     */
    private String getEmbersContextPath() {
        return "http://localhost:" + PORT + "/" + CONTEXT_PATH_ROOT;
    }
}
