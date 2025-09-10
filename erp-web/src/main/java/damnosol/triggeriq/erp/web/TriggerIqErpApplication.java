package damnosol.triggeriq.erp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "damnosol.triggeriq.erp")
public class TriggerIqErpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TriggerIqErpApplication.class, args);
    }
}