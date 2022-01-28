package adf.embers.tools;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.configuration.EmbersHandlerConfiguration;
import adf.embers.configuration.EmbersProcessorConfiguration;
import adf.embers.configuration.EmbersRepositoryConfiguration;
import adf.embers.query.QueryHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class EmbersJettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(EmbersJettyServer.class);

    private final int port;
    private Server server;

    public EmbersJettyServer(int port) {
        this.port = port;
    }

    public void startHttpServer(DataSource dataSource) throws Exception {
        LOG.info("Starting the Embers Server on port: " + port);

        // Configure Jetty
        final ResourceConfig config = new ResourceConfig();
        config.register(new EmbersBinder(dataSource));

//        server = JettyHttpContainerFactory.createServer(
//            URI.create("http://localhost:" + port + "/" + EmbersServer.CONTEXT_PATH_ROOT), config);

        server = new Server(port);
        ServletContextHandler ctx = new ServletContextHandler(
            ServletContextHandler.NO_SESSIONS);
        ctx.setContextPath("/embers");
        server.setHandler(ctx);
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        servlet.setInitOrder(1);
        ctx.addServlet(servlet, "/*");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down the application...");
                server.stop();
                System.out.println("Done, exit.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        server.start();
    }

    public void stopHttpServer() {
        LOG.info("Stopping the Embers server");
        try {
            server.stop();
        } catch (Exception e) {
            LOG.error("Exception stopping jetty server: " + e.getMessage());
        }
    }

    public static class EmbersBinder extends AbstractBinder {

        private final EmbersHandlerConfiguration embersConfiguration;

        public EmbersBinder(DataSource dataSource) {
            super();
            EmbersRepositoryConfiguration embersRepositoryConfiguration = new EmbersRepositoryConfiguration(dataSource);
            EmbersProcessorConfiguration embersProcessorConfiguration = new EmbersProcessorConfiguration(embersRepositoryConfiguration);
            embersConfiguration = new EmbersHandlerConfiguration(embersProcessorConfiguration);
        }

        @Override
        protected void configure() {
            LOG.info("Binding embers handlers");
            bind(embersConfiguration.getAdminQueryHandler()).to(AdminQueryHandler.class);
            bind(embersConfiguration.getQueryHandler()).to(QueryHandler.class);
            bind(embersConfiguration.getQueryResultCacheHandler()).to(QueryResultCacheHandler.class);
        }
    }
}
