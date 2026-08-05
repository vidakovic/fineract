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
package org.apache.fineract.command.makerchecker.hook;

import static org.apache.fineract.command.makerchecker.MakerCheckerCommandConstants.COMMAND_MAKER_CHECKER_HOOK_CHECK_BEFORE;
import static org.apache.fineract.command.makerchecker.MakerCheckerCommandConstants.COMMAND_MAKER_CHECKER_PROPERTY_HOOK_CHECK_PRE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.command.core.CommandContext;
import org.apache.fineract.command.core.CommandHookBefore;
import org.apache.fineract.command.core.CommandStore;
import org.apache.fineract.command.makerchecker.MakerCheckerCommandProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(COMMAND_MAKER_CHECKER_HOOK_CHECK_BEFORE)
@ConditionalOnProperty(value = COMMAND_MAKER_CHECKER_PROPERTY_HOOK_CHECK_PRE, havingValue = "true")
final class MakerCheckerCheckCommandHook implements CommandHookBefore<Object, Object> {

    private final CommandStore store;
    private final MakerCheckerCommandProperties properties;

    @Override
    public void onBefore(CommandContext<Object, Object> ctx) {
        var command = ctx.getCommand();

        // TODO: implement this!

        // 1. check if Maker-Checker is required for this specific action (e.g., CREATE_CLIENT)

        // 2. store the request as a PENDING command

        // 3. return to Maker: "Task created, pending approval"
    }
}
