package com.rbkmoney.fistful.reporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.rbkmoney.fistful.reporter"})
public class FistfulReporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FistfulReporterApplication.class, args);
    }

}
