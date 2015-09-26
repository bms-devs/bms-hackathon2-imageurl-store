package org.bmshackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
public class ImageurlStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageurlStoreApplication.class, args);
    }
}
