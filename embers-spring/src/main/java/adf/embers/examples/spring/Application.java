package adf.embers.examples.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(
        scanBasePackageClasses = {
                SpringConfiguration.class,
                EmbersSpringConfiguration.class
        })
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
        new Application()
                .configure(new SpringApplicationBuilder(Application.class))
                .run(args);

        System.out.println("started");
    }
}
