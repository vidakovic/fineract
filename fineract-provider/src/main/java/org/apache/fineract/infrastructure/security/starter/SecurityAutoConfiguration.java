/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.security.starter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.config.FineractProperties;
import org.apache.fineract.infrastructure.security.service.JdbcTenantDetailsService;
import org.apache.fineract.infrastructure.security.service.PropertiesTenantDetailsService;
import org.apache.fineract.infrastructure.security.service.TenantDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityAutoConfiguration {

    @Bean(name = "hikariTenantDataSource", destroyMethod = "close")
    @ConditionalOnProperty(value = "fineract.tenant.source", havingValue = "jdbc")
    @Deprecated // TODO: remove this when we switched exclusively to properties based configuration
    public DataSource hikariTenantDataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean("hikariTenantJdbcTemplate")
    @ConditionalOnProperty(value = "fineract.tenant.source", havingValue = "jdbc")
    @Deprecated // TODO: remove this when we switched exclusively to properties based configuration
    public JdbcTemplate hikariTenantJdbcTemplate(@Qualifier("hikariTenantDataSource") final DataSource dataSource) {
        log.warn(
                "WARNING!!! You are using an INSECURE implementation of the tenant management feature! Please consider migrating your database tenants to property files.");
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnProperty(value = "fineract.tenant.source", havingValue = "jdbc")
    @Deprecated // TODO: remove this when we switched exclusively to properties based configuration
    public TenantDetailsService jdbcDataSourcePerTenantService(@Qualifier("hikariTenantJdbcTemplate") final JdbcTemplate jdbcTemplate) {
        log.warn(
                "WARNING!!! You are using an INSECURE implementation of the tenant management feature! Please consider migrating your database tenants to property files.");
        return new JdbcTenantDetailsService(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(value = "fineract.tenant.source", havingValue = "properties")
    public TenantDetailsService propertiesTenantDetailsService(FineractProperties fineractProperties) {
        return new PropertiesTenantDetailsService(fineractProperties);
    }

    public static class FineractTenantSourceCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // TODO: implement this for more sophisticated decision on which tenant details service to use
            return false;
        }
    }
}
