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
package org.apache.fineract.infrastructure.core.service.migration;

import static org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection.toJdbcUrl;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Deprecated // TODO: remove this when we switched exclusively to properties based configuration
public class TenantDataSourceFactory {

    private final HikariDataSource tenantDataSource;

    private final DatabasePasswordEncryptor databasePasswordEncryptor;

    @Autowired
    public TenantDataSourceFactory(@Qualifier("hikariTenantDataSource") HikariDataSource tenantDataSource,
            DatabasePasswordEncryptor databasePasswordEncryptor) {
        this.tenantDataSource = tenantDataSource;
        this.databasePasswordEncryptor = databasePasswordEncryptor;
    }

    public DataSource create(FineractPlatformTenant tenant) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(tenantDataSource.getDriverClassName());
        dataSource.setDataSourceProperties(tenantDataSource.getDataSourceProperties());
        dataSource.setMinimumIdle(tenantDataSource.getMinimumIdle());
        dataSource.setMaximumPoolSize(tenantDataSource.getMaximumPoolSize());
        dataSource.setIdleTimeout(tenantDataSource.getIdleTimeout());
        dataSource.setConnectionTestQuery(tenantDataSource.getConnectionTestQuery());

        FineractPlatformTenantConnection tenantConnection = tenant.getConnection();
        if (!databasePasswordEncryptor.isMasterPasswordHashValid(tenantConnection.getMasterPasswordHash())) {
            throw new IllegalArgumentException("Invalid master password");
        }
        dataSource.setUsername(tenantConnection.getSchemaUsername());
        dataSource.setPassword(databasePasswordEncryptor.decrypt(tenantConnection.getSchemaPassword()));
        String protocol = StringUtils.isEmpty(tenantConnection.getSchemaProtocol()) ? toProtocol(tenantDataSource)
                : tenantConnection.getSchemaProtocol();
        String tenantJdbcUrl = toJdbcUrl(protocol, tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(),
                tenantConnection.getSchemaName(), tenantConnection.getSchemaConnectionParameters());
        // TODO: remove this, could be security relevant if someone sets the password via URL query parameter
        log.debug("JDBC URL for tenant {} is {}", tenant.getTenantIdentifier(), tenantJdbcUrl);
        dataSource.setJdbcUrl(tenantJdbcUrl);
        return dataSource;
    }

    private String toProtocol(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            return url.substring(0, url.indexOf("://"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
