package com.example.resinoracle.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DataSourceConfig.class)
@ComponentScan(basePackages = {
        "com.example.resinoracle.service",
        "com.example.resinoracle.repository"
})
public class RootConfig {
}

