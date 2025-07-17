package com.microservices.apigateway.config;

import com.microservices.apigateway.loadbalancer.WeightedRoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
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

    @Bean
    public ReactorServiceInstanceLoadBalancer userServiceLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            LoadBalancerWeightsProperties weightsProperties) {
        return new WeightedRoundRobinLoadBalancer(
                serviceInstanceListSupplierProvider.getIfAvailable(),
                "user-service",
                weightsProperties
        );
    }

    @Bean
    public ReactorServiceInstanceLoadBalancer orderServiceLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            LoadBalancerWeightsProperties weightsProperties) {
        return new WeightedRoundRobinLoadBalancer(
                serviceInstanceListSupplierProvider.getIfAvailable(),
                "order-service",
                weightsProperties
        );
    }
} 