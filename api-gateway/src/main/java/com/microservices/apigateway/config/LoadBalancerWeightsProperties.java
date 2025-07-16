package com.microservices.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "loadbalancer.weights")
public class LoadBalancerWeightsProperties {
    /**
     * Map of serviceId -> (instanceId -> weight)
     * Example property: loadbalancer.weights.product-service[localhost:8081]=3
     */
    private Map<String, Map<String, Integer>> services = new HashMap<>();

    public Map<String, Map<String, Integer>> getServices() {
        return services;
    }

    public void setServices(Map<String, Map<String, Integer>> services) {
        this.services = services;
    }

    /**
     * Get weights for a specific serviceId.
     */
    public Map<String, Integer> getWeightsForService(String serviceId) {
        return services.getOrDefault(serviceId, new HashMap<>());
    }
} 