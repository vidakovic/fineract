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
package org.apache.fineract.apachecon.note.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apache.fineract.apachecon.command.service.CommandHandler;
import org.apache.fineract.apachecon.note.data.NoteUpdateRequest;
import org.apache.fineract.apachecon.note.data.NoteUpdateResponse;
import org.apache.fineract.portfolio.note.service.NoteWritePlatformService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class NoteUpdateCommandHandler implements CommandHandler<NoteUpdateRequest, NoteUpdateResponse> {

    private final NoteWritePlatformService noteWriteService;

    @Override
    public boolean accept(Class<? extends CommandRequest> clazz) {
        return clazz.equals(NoteUpdateRequest.class);
    }

    @Transactional
    @Override
    public NoteUpdateResponse handle(NoteUpdateRequest request) {
        var result = noteWriteService.updateNote(request.getBody());

        var response = new NoteUpdateResponse();

        response.setRequestId(request.getId());
        response.setUserId(request.getUserId());
        response.setTenantId(request.getTenantId());
        response.setCreatedAt(Instant.now());
        response.setBody(result);

        return response;
    }
}
