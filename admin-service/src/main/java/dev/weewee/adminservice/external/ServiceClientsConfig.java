package dev.weewee.adminservice.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceClientsConfig {

    @Value("${services.order-service.base-url}")
    private String orderServiceUrl;

    @Value("${services.payment-service.base-url}")
    private String paymentServiceUrl;

    @Value("${services.delivery-service.base-url}")
    private String deliveryServiceUrl;

    @Value("${services.user-service.base-url}")
    private String userServiceUrl;

    @Bean("orderWebClient")
    public WebClient orderWebClient() {
        return WebClient.builder()
                .baseUrl(orderServiceUrl)
                .build();
    }

    @Bean("paymentWebClient")
    public WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServiceUrl)
                .build();
    }

    @Bean("deliveryWebClient")
    public WebClient deliveryWebClient() {
        return WebClient.builder()
                .baseUrl(deliveryServiceUrl)
                .build();
    }

    @Bean("userWebClient")
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}

