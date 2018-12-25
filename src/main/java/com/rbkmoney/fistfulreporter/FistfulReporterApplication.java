package com.rbkmoney.fistfulreporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.rbkmoney.fistfulreporter"})
public class FistfulReporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FistfulReporterApplication.class, args);
    }

}
