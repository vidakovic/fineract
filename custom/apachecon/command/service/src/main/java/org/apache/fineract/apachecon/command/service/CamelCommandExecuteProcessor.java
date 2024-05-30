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

import static org.apache.fineract.commands.domain.CommandProcessingResultType.PROCESSED;
import static org.apache.fineract.commands.service.SynchronousCommandProcessingService.IDEMPOTENCY_KEY_STORE_FLAG;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.command.data.CamelCommandExecuteRequest;
import org.apache.fineract.apachecon.command.mapping.CamelCommandProcessingResultMapper;
import org.apache.fineract.apachecon.command.mapping.CamelCommandWrapperMapper;
import org.apache.fineract.apachecon.command.mapping.CamelJsonCommandMapper;
import org.apache.fineract.commands.domain.CommandProcessingResultType;
import org.apache.fineract.commands.domain.CommandSource;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.exception.UnsupportedCommandException;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.commands.provider.CommandHandlerProvider;
import org.apache.fineract.commands.service.CommandSourceService;
import org.apache.fineract.commands.service.IdempotencyKeyResolver;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.BatchRequestContextHolder;
import org.apache.fineract.infrastructure.core.domain.FineractRequestContextHolder;
import org.apache.fineract.infrastructure.core.exception.IdempotentCommandProcessFailedException;
import org.apache.fineract.infrastructure.core.exception.IdempotentCommandProcessSucceedException;
import org.apache.fineract.infrastructure.core.exception.IdempotentCommandProcessUnderProcessingException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CamelCommandExecuteProcessor {

    public static final String COMMAND_SOURCE_ID = "commandSourceId";
    private final PlatformSecurityContext context;
    private final ApplicationContext applicationContext;
    private final ConfigurationDomainService configurationDomainService;
    private final CommandHandlerProvider commandHandlerProvider;
    private final IdempotencyKeyResolver idempotencyKeyResolver;
    private final CommandSourceService commandSourceService;
    private final FineractRequestContextHolder fineractRequestContextHolder;
    private final CamelCommandWrapperMapper commandWrapperMapper;
    private final CamelJsonCommandMapper jsonCommandMapper;
    private final CamelCommandProcessingResultMapper commandProcessingResultMapper;
    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public CamelCommandExecuteRequest execute(CamelCommandExecuteRequest request) {
        // Do not store the idempotency key because of the exception handling
        fineractRequestContextHolder.setAttribute(IDEMPOTENCY_KEY_STORE_FLAG, false);

        Long commandId = (Long) fineractRequestContextHolder.getAttribute(COMMAND_SOURCE_ID, null);
        boolean isRetry = commandId != null;
        boolean isEnclosingTransaction = BatchRequestContextHolder.isEnclosingTransaction();
        CommandSource commandSource = null;
        var commandWrapper = request.getWrapper();
        var jsonCommand = request.getCommand();
        String idempotencyKey;

        if (isRetry) {
            commandSource = commandSourceService.getCommandSource(commandId);
            idempotencyKey = commandSource.getIdempotencyKey();
        } else if ((commandId = request.getCommand().commandId()) != null) { // action on the command itself
            commandSource = commandSourceService.getCommandSource(commandId);
            idempotencyKey = commandSource.getIdempotencyKey();
        } else {
            idempotencyKey = idempotencyKeyResolver.resolve(commandWrapper);
        }

        exceptionWhenTheRequestAlreadyProcessed(commandWrapper, idempotencyKey, isRetry);

        AppUser user = context.authenticatedUser(commandWrapper);
        if (commandSource == null) {
            if (isEnclosingTransaction) {
                commandSource = commandSourceService.getInitialCommandSource(commandWrapper, jsonCommand, user, idempotencyKey);
            } else {
                commandSource = commandSourceService.saveInitialNewTransaction(commandWrapper, jsonCommand, user, idempotencyKey);
                commandId = commandSource.getId();
            }
        }
        if (commandId != null) {
            storeCommandIdInContext(commandSource); // Store command id as a request attribute
        }

        boolean isMakerChecker = configurationDomainService.isMakerCheckerEnabledForTask(request.getWrapper().getTaskPermissionName());
        if (request.isApprovedByChecker() || (isMakerChecker && user.isCheckerSuperUser())) {
            commandSource.markAsChecked(user);
        }

        fineractRequestContextHolder.setAttribute(IDEMPOTENCY_KEY_STORE_FLAG, true);

        final CommandProcessingResult result = commandSourceService.processCommand(findCommandHandler(commandWrapper), jsonCommand,
                commandSource, user, request.isApprovedByChecker(), isMakerChecker);

        commandSource.setResultStatusCode(SC_OK);
        commandSource.updateForAudit(result);
        commandSource.setResult(mapper.writeValueAsString(result));
        commandSource.setStatus(PROCESSED);
        commandSource = commandSourceService.saveResultSameTransaction(commandSource);

        storeCommandIdInContext(commandSource); // Store command id as a request attribute

        result.setRollbackTransaction(null);

        return request.toBuilder().result(result).commandSource(commandSource).build();
    }

    private void exceptionWhenTheRequestAlreadyProcessed(CommandWrapper wrapper, String idempotencyKey, boolean retry) {
        CommandSource command = commandSourceService.findCommandSource(wrapper, idempotencyKey);
        if (command == null) {
            return;
        }
        CommandProcessingResultType status = CommandProcessingResultType.fromInt(command.getStatus());
        switch (status) {
            case UNDER_PROCESSING -> throw new IdempotentCommandProcessUnderProcessingException(wrapper, idempotencyKey);
            case PROCESSED -> throw new IdempotentCommandProcessSucceedException(wrapper, idempotencyKey, command);
            case ERROR -> {
                if (!retry) {
                    throw new IdempotentCommandProcessFailedException(wrapper, idempotencyKey, command);
                }
            }
            default -> {
            }
        }
    }

    private void storeCommandIdInContext(CommandSource savedCommandSource) {
        if (savedCommandSource.getId() == null) {
            throw new IllegalStateException("Command source not saved");
        }
        // Idempotency filters and retry need this
        fineractRequestContextHolder.setAttribute(COMMAND_SOURCE_ID, savedCommandSource.getId());
    }

    private NewCommandSourceHandler findCommandHandler(final CommandWrapper wrapper) {
        NewCommandSourceHandler handler;

        if (wrapper.isDatatableResource()) {
            if (wrapper.isCreateDatatable()) {
                handler = applicationContext.getBean("createDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteDatatable()) {
                handler = applicationContext.getBean("deleteDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateDatatable()) {
                handler = applicationContext.getBean("updateDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isCreate()) {
                handler = applicationContext.getBean("createDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateMultiple()) {
                handler = applicationContext.getBean("updateOneToManyDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateOneToOne()) {
                handler = applicationContext.getBean("updateOneToOneDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteMultiple()) {
                handler = applicationContext.getBean("deleteOneToManyDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteOneToOne()) {
                handler = applicationContext.getBean("deleteOneToOneDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isRegisterDatatable()) {
                handler = applicationContext.getBean("registerDatatableCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isNoteResource()) {
            if (wrapper.isCreate()) {
                handler = applicationContext.getBean("createNoteCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdate()) {
                handler = applicationContext.getBean("updateNoteCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDelete()) {
                handler = applicationContext.getBean("deleteNoteCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isSurveyResource()) {
            if (wrapper.isRegisterSurvey()) {
                handler = applicationContext.getBean("registerSurveyCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isFullFilSurvey()) {
                handler = applicationContext.getBean("fullFilSurveyCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isLoanDisburseDetailResource()) {
            if (wrapper.isUpdateDisbursementDate()) {
                handler = applicationContext.getBean("updateLoanDisburseDateCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.addAndDeleteDisbursementDetails()) {
                handler = applicationContext.getBean("addAndDeleteLoanDisburseDetailsCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else {
            handler = commandHandlerProvider.getHandler(wrapper.entityName(), wrapper.actionName());
        }

        return handler;
    }
}
