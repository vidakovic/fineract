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
package org.apache.fineract.apachecon.command.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.command.data.CamelCommandExecuteRequest;
import org.apache.fineract.apachecon.command.mapping.CamelJsonCommandMapper;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.hooks.event.HookEvent;
import org.apache.fineract.infrastructure.hooks.event.HookEventSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CamelCommandHookEventProcessor {

    private final PlatformSecurityContext securityContext;
    private final CamelJsonCommandMapper jsonCommandMapper;
    private final ObjectMapper mapper = new ObjectMapper(); // TODO: if this needs further configuration

    @SneakyThrows
    public HookEvent publish(CamelCommandExecuteRequest request) {
        final var entityName = request.getWrapper().getEntityName();
        final var actionName = request.getWrapper().getActionName();
        final var command = request.getCommand();

        final AppUser appUser = securityContext.authenticatedUser(CommandWrapper.wrap(actionName, entityName, null, null));

        final HookEventSource hookEventSource = new HookEventSource(entityName, actionName);

        if (command.json() != null) {
            Map<String, Object> myMap;

            try {
                myMap = mapper.readValue(command.json(), new TypeReference<>() {});
            } catch (Exception e) {
                throw new PlatformApiDataValidationException("error.msg.invalid.json", "The provided JSON is invalid.", new ArrayList<>(),
                        e);
            }

            Map<String, Object> reqmap = new HashMap<>();
            reqmap.put("entityName", entityName);
            reqmap.put("actionName", actionName);
            reqmap.put("createdBy", securityContext.authenticatedUser().getId());
            reqmap.put("createdByName", securityContext.authenticatedUser().getUsername());
            reqmap.put("createdByFullName", securityContext.authenticatedUser().getDisplayName());

            reqmap.put("request", myMap);
            if (request.getResult() != null) {
                reqmap.put("officeId", request.getResult().getOfficeId());
                reqmap.put("clientId", request.getResult().getClientId());
                request.getResult().setOfficeId(null);
                reqmap.put("response", request.getResult());
            } else if (request.getErrorInfo() != null) {
                reqmap.put("status", "Exception");

                Map<String, Object> errorMap = new HashMap<>();

                try {
                    errorMap = mapper.readValue(request.getErrorInfo().getMessage(), new TypeReference<>() {});
                } catch (Exception e) {
                    errorMap.put("errorMessage", request.getErrorInfo().getMessage());
                }

                errorMap.put("errorCode", request.getErrorInfo().getErrorCode());
                errorMap.put("statusCode", request.getErrorInfo().getStatusCode());

                reqmap.put("response", errorMap);
            }

            reqmap.put("timestamp", Instant.now().toString());

            return new HookEvent(hookEventSource, mapper.writeValueAsString(reqmap), appUser, ThreadLocalContextUtil.getContext());
        }

        return null;
    }
}
