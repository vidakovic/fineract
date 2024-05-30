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

import static org.apache.fineract.commands.domain.CommandProcessingResultType.ERROR;
import static org.apache.http.HttpStatus.SC_OK;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.fineract.apachecon.command.data.CamelCommandExecuteRequest;
import org.apache.fineract.apachecon.command.mapping.CamelErrorInfoMapper;
import org.apache.fineract.batch.exception.ErrorInfo;
import org.apache.fineract.commands.domain.CommandSource;
import org.apache.fineract.commands.service.CommandSourceService;
import org.apache.fineract.infrastructure.core.domain.BatchRequestContextHolder;
import org.apache.fineract.infrastructure.core.exception.ErrorHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CamelCommandErrorProcessor {

    private final CommandSourceService commandSourceService;
    private final CamelErrorInfoMapper errorInfoMapper;

    public CamelCommandExecuteRequest onError(Exchange exchange) {
        Throwable t = exchange.getException();
        CamelCommandExecuteRequest request = exchange.getMessage(CamelCommandExecuteRequest.class);
        CommandSource commandSource = request.getCommandSource();

        RuntimeException mappable = ErrorHandler.getMappable(t);
        ErrorInfo errorInfo = commandSourceService.generateErrorInfo(mappable);
        Integer statusCode = errorInfo.getStatusCode();
        commandSource.setResultStatusCode(statusCode);
        commandSource.setResult(errorInfo.getMessage());

        if (statusCode != SC_OK) {
            commandSource.setStatus(ERROR);
        }

        if (!BatchRequestContextHolder.isEnclosingTransaction()) {
            commandSource = commandSourceService.saveResultNewTransaction(commandSource);
        }

        // must not throw any exception; must persist in new transaction as the current transaction was already
        // marked as rollback
        return request.toBuilder().commandSource(commandSource).errorInfo(errorInfo).build();
    }
}
