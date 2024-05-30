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

import static org.apache.fineract.apachecon.command.service.CommandConstants.FINERACT_ROUTE_COMMAND_EXECTUE;
import static org.apache.fineract.apachecon.command.service.CommandConstants.FINERACT_ROUTE_COMMAND_VALIDATE_ROLLBACK;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.fineract.apachecon.command.data.CamelCommandExecuteRequest;
import org.apache.fineract.apachecon.command.data.CamelCommandValidateRequest;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandProcessingService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamelCommandProcessingService implements CommandProcessingService {

    private final ProducerTemplate producerTemplate;

    @PostConstruct
    public void init() {
        log.warn(">>>>>>>>>>>>>>>>>> Community over Code 2024: Camel Command Processing Service!!!");
    }

    @Override
    public CommandProcessingResult executeCommand(CommandWrapper wrapper, JsonCommand command, boolean isApprovedByChecker) {
        var response = (CamelCommandExecuteRequest) producerTemplate.sendBody("direct://" + FINERACT_ROUTE_COMMAND_EXECTUE,
                ExchangePattern.InOut,
                CamelCommandExecuteRequest.builder().wrapper(wrapper).command(command).approvedByChecker(isApprovedByChecker).build());

        return response.getResult();
    }

    @Override
    public boolean validateRollbackCommand(CommandWrapper wrapper, AppUser user) {
        var response = (CamelCommandValidateRequest) producerTemplate.sendBody("direct://" + FINERACT_ROUTE_COMMAND_VALIDATE_ROLLBACK,
                ExchangePattern.InOut, CamelCommandValidateRequest.builder().wrapper(wrapper).user(user).build());

        return response.isValidated();
    }
}
