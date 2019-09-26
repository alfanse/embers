package adf.embers.examples.spring;

import adf.embers.configuration.EmbersHandlerConfiguration;
import adf.embers.configuration.EmbersProcessorConfiguration;
import adf.embers.configuration.EmbersRepositoryConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EmbersSpringConfiguration {

    public static final String EMBERS = "embers";

    @Autowired
    DataSource dataSource;

    @Bean
    EmbersHandlerConfiguration embersHandlerConfiguration() {
        EmbersRepositoryConfiguration embersRepositoryConfiguration = new EmbersRepositoryConfiguration(dataSource);
        EmbersProcessorConfiguration embersProcessorConfiguration = new EmbersProcessorConfiguration(embersRepositoryConfiguration);
        return new EmbersHandlerConfiguration(embersProcessorConfiguration);
    }

    @Bean
    @Qualifier(EMBERS)
    public ResourceConfig resourceConfig(
            @Autowired EmbersHandlerConfiguration handlerConfiguration
    ) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(handlerConfiguration.getQueryHandler());
        resourceConfig.register(handlerConfiguration.getAdminQueryHandler());
        resourceConfig.register(handlerConfiguration.getQueryResultCacheHandler());
        return resourceConfig;
    }

    /**
     * Registers the ResourceConfig to path /embers/*
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean(
            @Autowired @Qualifier(EMBERS) ResourceConfig resourceConfig
    ) {
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        return new ServletRegistrationBean(servletContainer, "/embers/*");
    }
}
