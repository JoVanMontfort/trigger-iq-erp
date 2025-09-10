package damnosol.triggeriq.erp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "damnosol.triggeriq.erp.infra.storage," +
        "damnosol.triggeriq.erp.core.application.service," +
        "damnosol.triggeriq.erp.adapters.web.controller," +
        "damnosol.triggeriq.erp.infra.config," +
        "damnosol.triggeriq.erp.infra.parser")
public class TriggerIqErpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TriggerIqErpApplication.class, args);
    }
}