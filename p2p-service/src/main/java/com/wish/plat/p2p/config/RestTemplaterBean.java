package com.wish.plat.p2p.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@SpringBootConfiguration
public class RestTemplaterBean {

   @Bean
   public RestTemplate restTemplate () {
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
      return restTemplate;
   }
}
