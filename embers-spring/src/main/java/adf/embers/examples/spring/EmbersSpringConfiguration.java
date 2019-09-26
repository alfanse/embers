package adf.embers.examples.spring;

import adf.embers.configuration.EmbersHandlerConfiguration;
import adf.embers.configuration.EmbersProcessorConfiguration;
import adf.embers.configuration.EmbersRepositoryConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

@Configuration
public class EmbersSpringConfiguration {

    @Autowired
    DataSource dataSource;

    @Autowired
    ServletRegistrationBean jerseyServletRegistration;

    @Autowired
    ServletContext servletContext;

    @Bean EmbersHandlerConfiguration embersHandlerConfiguration() {
        EmbersRepositoryConfiguration embersRepositoryConfiguration = new EmbersRepositoryConfiguration(dataSource);
        EmbersProcessorConfiguration embersProcessorConfiguration = new EmbersProcessorConfiguration(embersRepositoryConfiguration);
        return new EmbersHandlerConfiguration(embersProcessorConfiguration);
    }

    @Bean
    ResourceConfig resourceConfig(
            @Autowired EmbersHandlerConfiguration handlerConfiguration
    ) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(handlerConfiguration.getQueryHandler());
        resourceConfig.register(handlerConfiguration.getAdminQueryHandler());
        resourceConfig.register(handlerConfiguration.getQueryResultCacheHandler());
        return resourceConfig;
    }

    @Bean
    org.springframework.boot.web.servlet.ServletContextInitializer servletContextInitializer(
            @Autowired ResourceConfig resourceConfig
    ){
        ServletContainer servlet = new ServletContainer(resourceConfig);
        return servletContext -> {
            servletContext.addServlet("embers", servlet);
        };
    }

    @PostConstruct
    void initContext() throws ServletException {
        jerseyServletRegistration.onStartup(servletContext);
    }

}
