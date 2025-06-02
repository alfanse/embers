package adf.embers.examples.spring;

import adf.embers.configuration.EmbersHandlerConfiguration;
import adf.embers.configuration.EmbersProcessorConfiguration;
import adf.embers.configuration.EmbersRepositoryConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EmbersSpringConfiguration {
    public static final String EMBERS = "embers";

    @Bean
    public EmbersRepositoryConfiguration embersRepositoryConfiguration(DataSource dataSource) {
        return new EmbersRepositoryConfiguration(dataSource);
    }

    @Bean
    public EmbersProcessorConfiguration embersProcessorConfiguration(EmbersRepositoryConfiguration embersRepositoryConfiguration) {
        return new EmbersProcessorConfiguration(embersRepositoryConfiguration);
    }

    @Bean
    public EmbersHandlerConfiguration embersHandlerConfiguration(EmbersProcessorConfiguration embersProcessorConfiguration) {
        return new EmbersHandlerConfiguration(
            embersProcessorConfiguration.queryDao(),
            embersProcessorConfiguration.queryProcessor(),
            embersProcessorConfiguration.cachedQueryProcessor()
        );
    }

    @Bean
    @org.springframework.beans.factory.annotation.Qualifier(EMBERS)
    public ResourceConfig resourceConfig(EmbersHandlerConfiguration handlerConfiguration) {
        ResourceConfig resourceConfig = new ResourceConfig();
        
        // Register the handlers
        resourceConfig.register(handlerConfiguration.getQueryHandler());
        resourceConfig.register(handlerConfiguration.getAdminQueryHandler());
        resourceConfig.register(handlerConfiguration.getQueryResultCacheHandler());
        
        // Configure Jersey
        resourceConfig.packages("adf.embers");
        resourceConfig.property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
        resourceConfig.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        
        // Enable Jackson JSON processing
        resourceConfig.register(org.glassfish.jersey.jackson.JacksonFeature.class);
        resourceConfig.register(com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider.class);
        
        return resourceConfig;
    }

    @Bean
    public ServletRegistrationBean<ServletContainer> servletRegistrationBean(
            @org.springframework.beans.factory.annotation.Qualifier(EMBERS) ResourceConfig resourceConfig) {
            
        // Create and configure the servlet
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletRegistrationBean<ServletContainer> registration = new ServletRegistrationBean<>(
            servletContainer, "/" + EMBERS + "/*");
        registration.setName("jersey-servlet");
        registration.setLoadOnStartup(1);
        return registration;
    }
}
