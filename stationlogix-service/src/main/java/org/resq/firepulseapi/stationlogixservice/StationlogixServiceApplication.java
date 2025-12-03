package org.resq.firepulseapi.stationlogixservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StationlogixServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StationlogixServiceApplication.class, args);
    }

}
