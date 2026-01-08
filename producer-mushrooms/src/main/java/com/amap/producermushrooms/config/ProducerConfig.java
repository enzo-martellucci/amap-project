package com.amap.producermushrooms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "producer")
@Data
public class ProducerConfig {
    private String id;
    private String name;
}