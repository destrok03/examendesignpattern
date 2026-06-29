package com.badwallet.wallet.config;

import com.badwallet.wallet.strategy.FeeStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Bean RestTemplate pour appeler payment-service
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Bean FeeStrategyFactory injecté par Spring (IoC)
    @Bean
    public FeeStrategyFactory feeStrategyFactory() {
        return new FeeStrategyFactory();
    }
}
