package dev.vality.fistful.reporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"dev.vality.fistful.reporter"})
@EnableScheduling
public class FistfulReporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FistfulReporterApplication.class, args);
    }

}
