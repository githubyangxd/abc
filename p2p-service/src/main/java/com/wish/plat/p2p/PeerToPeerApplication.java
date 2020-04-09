package com.wish.plat.p2p;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableCaching
@ImportResource(value = {"classpath*:rpc-start-p2p-service.xml"})
@ComponentScan(basePackages = {"com.wish.plat"})
public class PeerToPeerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeerToPeerApplication.class, args);
    }

}
