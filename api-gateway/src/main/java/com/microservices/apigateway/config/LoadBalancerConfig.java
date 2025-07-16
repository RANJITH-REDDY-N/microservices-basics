package com.microservices.apigateway.config;

import com.microservices.apigateway.loadbalancer.WeightedRoundRobinLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;

@Configuration
public class LoadBalancerConfig {

    /**
     * Register the custom WeightedRoundRobinLoadBalancer for product-service.
     * You can duplicate this bean for other services as needed.
     */
    @Bean
    public ReactorServiceInstanceLoadBalancer productServiceLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            LoadBalancerWeightsProperties weightsProperties) {
        return new WeightedRoundRobinLoadBalancer(
                serviceInstanceListSupplierProvider.getIfAvailable(),
                "product-service",
                weightsProperties
        );
    }

    // To add for other services, duplicate the above bean with the appropriate serviceId.
} 