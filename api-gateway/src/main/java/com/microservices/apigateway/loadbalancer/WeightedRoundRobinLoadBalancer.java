package com.microservices.apigateway.loadbalancer;

import com.microservices.apigateway.config.LoadBalancerWeightsProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Weighted Round Robin Load Balancer for Spring Cloud Gateway.
 * Uses weights from LoadBalancerWeightsProperties to distribute requests.
 */
public class WeightedRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private final String serviceId;
    private final ServiceInstanceListSupplier serviceInstanceListSupplier;
    private final LoadBalancerWeightsProperties weightsProperties;

    // Keeps track of the current index and weight for each serviceId
    private final Map<String, AtomicInteger> currentIndexes = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> currentWeights = new ConcurrentHashMap<>();

    public WeightedRoundRobinLoadBalancer(ServiceInstanceListSupplier serviceInstanceListSupplier,
                                          String serviceId,
                                          LoadBalancerWeightsProperties weightsProperties) {
        this.serviceInstanceListSupplier = serviceInstanceListSupplier;
        this.serviceId = serviceId;
        this.weightsProperties = weightsProperties;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return serviceInstanceListSupplier.get().next().map(this::selectInstance);
    }

    private Response<ServiceInstance> selectInstance(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return new EmptyResponse();
        }
        Map<String, Integer> weights = weightsProperties.getWeightsForService(serviceId);
        // Build a list of instances with their weights (default to 1 if not set)
        List<InstanceWeight> weightedInstances = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            String instanceId = instance.getHost() + ":" + instance.getPort();
            int weight = weights.getOrDefault(instanceId, 1);
            weightedInstances.add(new InstanceWeight(instance, weight));
        }
        if (weightedInstances.size() == 1) {
            return new DefaultResponse(weightedInstances.get(0).instance);
        }
        // Weighted round robin selection
        int totalWeight = weightedInstances.stream().mapToInt(iw -> iw.weight).sum();
        String key = serviceId;
        AtomicInteger index = currentIndexes.computeIfAbsent(key, k -> new AtomicInteger(0));
        AtomicInteger currentWeight = currentWeights.computeIfAbsent(key, k -> new AtomicInteger(0));
        int idx = index.getAndUpdate(i -> (i + 1) % totalWeight);
        int sum = 0;
        for (InstanceWeight iw : weightedInstances) {
            sum += iw.weight;
            if (idx < sum) {
                return new DefaultResponse(iw.instance);
            }
        }
        // Fallback (should not happen)
        return new DefaultResponse(weightedInstances.get(0).instance);
    }

    private static class InstanceWeight {
        ServiceInstance instance;
        int weight;
        InstanceWeight(ServiceInstance instance, int weight) {
            this.instance = instance;
            this.weight = weight;
        }
    }
} 