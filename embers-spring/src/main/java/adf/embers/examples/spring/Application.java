package adf.embers.examples.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackageClasses = {
                EmbersSpringConfiguration.class
        }
)
public class Application {

    /** main is required by spring boot, otherwise I'd have moved it to src/test/java */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("started");
    }
}
