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
import static org.apache.fineract.apachecon.command.service.CommandConstants.FINERACT_ROUTE_HOOK_ERROR_EVENT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CamelCommandProcessingRoute extends RouteBuilder {

    private final CamelCommandExecuteProcessor commandExecuteProcessor;
    private final CamelCommandValidateRollbackProcessor commandValidateRollbackProcessor;
    private final CamelCommandHookEventProcessor commandHookEventProcessor;

    @Override
    public void configure() throws Exception {
        from("direct://" + FINERACT_ROUTE_COMMAND_EXECTUE).log(LoggingLevel.WARN, "COMMAND EXECUTE: ${body}").bean(commandExecuteProcessor);

        from("direct://" + FINERACT_ROUTE_COMMAND_VALIDATE_ROLLBACK).threads(5, 15).onException(Exception.class).handled(true)
                .log(LoggingLevel.ERROR, "COMMAND VALIDATE ROLLBACK: ${exception}").end().bean(commandValidateRollbackProcessor);

        from("direct://" + FINERACT_ROUTE_HOOK_ERROR_EVENT).threads(5, 15).onException(Exception.class).handled(true)
                .log(LoggingLevel.ERROR, "HOOK ERROR EVENT: ${exception}").end().bean(commandHookEventProcessor).filter(body().isNotNull())
                .to("spring-event:" + FINERACT_ROUTE_HOOK_ERROR_EVENT);
    }
}
