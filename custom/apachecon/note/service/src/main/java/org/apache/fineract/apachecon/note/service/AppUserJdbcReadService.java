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
package org.apache.fineract.apachecon.note.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.note.data.AppUserJdbcData;
import org.apache.fineract.apachecon.note.domain.AppUserJdbcRepository;
import org.apache.fineract.apachecon.note.mapping.AppUserJdbcMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "userCache")
public class AppUserJdbcReadService /* implements AppUserReadPlatformService */ {

    private final AppUserJdbcRepository appUserJdbcRepository;

    private final AppUserJdbcMapper appUserJdbcMapper;

    @PostConstruct
    public void init() {
        log.warn(">>>>>>>>>>>>>>>>>> Community over Code 2024: App User Service!!!");
    }

    @Cacheable
    public AppUserJdbcData retrieveUser(Long id) {
        return appUserJdbcMapper.map(appUserJdbcRepository.findById(id).orElse(null));
    }
}
