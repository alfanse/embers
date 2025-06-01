package adf.embers.tools;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
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
        System.out.println("Starting Embers server...");
        try {
            startDatabase();
            System.out.println("Database started successfully");
            
            System.out.println("Starting Jetty server with data source: " + embersDatabase.getDataSource());
            startJettyServer(embersDatabase.getDataSource());
            
            // Add a small delay to ensure server is fully started
            Thread.sleep(1000);
            System.out.println("Embers server started successfully on port " + PORT);
            System.out.println("Query path: " + embersQueryPath());
            System.out.println("Admin path: " + embersAdminPath());
        } catch (Throwable t) {
            System.err.println("Error starting Embers server: " + t.getMessage());
            t.printStackTrace();
            throw t;
        }
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

    public String embersAdminPath() {
        return getEmbersContextPath() + AdminQueryHandler.PATH;
    }

    public String embersQueryPath() {
        return getEmbersContextPath() + QueryHandler.PATH;
    }

    public String embersCachedQueryPath() {
        return getEmbersContextPath() + QueryResultCacheHandler.PATH;
    }

    /**
     * starting from http:// up to /embers
     */
    private String getEmbersContextPath() {
        return "http://localhost:" + PORT + "/" + CONTEXT_PATH_ROOT;
    }
}
