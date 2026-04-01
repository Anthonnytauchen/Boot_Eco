package project.booteco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BootEcoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootEcoApplication.class, args);
    }

}
