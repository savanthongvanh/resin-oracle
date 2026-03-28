package com.example.resinoracle.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class DataSourceConfig {

    // Pool settings, credentials, and JDBC URL are owned by Resin, not the WAR.
    public static final String JNDI_NAME = "java:comp/env/jdbc/oracle/states";

    @Bean(name = "oracleDataSource")
    public DataSource oracleDataSource() throws Exception {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName(JNDI_NAME);
        factoryBean.setProxyInterface(DataSource.class);
        factoryBean.setLookupOnStartup(true);
        factoryBean.afterPropertiesSet();
        return (DataSource) factoryBean.getObject();
    }
}
