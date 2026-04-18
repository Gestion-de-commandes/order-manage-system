/*
package com.mproduits.configurations;

 * Author  : essejacaues.co
 * Date    : 06/03/2023 00:55
 * Project : spring-boot-microservices $
 
import brave.sampler.Sampler;
import org.springframework.boot.actuate.health.SimpleStatusAggregator;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SleuthConfig

{
    @Bean
    public Sampler defaultSampler()

    {
        return Sampler.ALWAYS_SAMPLE;
    }

    @Bean
    public StatusAggregator statusAggregator() {
        return new SimpleStatusAggregator();
    }
}
*/