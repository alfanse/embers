package adf.embers.tools;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryHandler;

import javax.sql.DataSource;

/* investigate http://crunchify.com/how-to-start-embedded-http-jersey-server-during-java-application-startup/
to replace jetty with jersey server */

public class EmbersServer {

    public static final int PORT = 8001;
    public static final String CONTEXT_PATH_ROOT = "embers";

    private EmbersDatabase embersDatabase;
    private EmbersJettyServer embersJettyServer;

    public void before() throws Throwable {
        startDatabase();
        startJettyServer(embersDatabase.getDataSource());
    }

    @SuppressWarnings("unused") //Keeping it as a might be needed to fix re-using server bugs
    public void after() {
        embersJettyServer.stopHttpServer();
        embersDatabase.shutdownInMemoryDatabase();
    }

    private void startDatabase() throws Exception {
        embersDatabase = new EmbersDatabase(EmbersDatabase.JDBC_URL);
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueries();
        embersDatabase.createTableQueriesStatistics();
        embersDatabase.createTableQueryResultCache();
    }

    private void startJettyServer(DataSource dataSource) throws Exception {
        embersJettyServer = new EmbersJettyServer(EmbersServer.PORT);
        embersJettyServer.startHttpServer(dataSource);
    }

    public EmbersDatabase getEmbersDatabase() {
        return embersDatabase;
    }

    public String embersCachedQueryPath() {
        return getEmbersContextPath() + "/" + QueryHandler.PATH+"/"+"cached";
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
