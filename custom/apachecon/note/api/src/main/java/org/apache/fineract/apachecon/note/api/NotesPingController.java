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
package org.apache.fineract.apachecon.note.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
@RequestMapping(value = "/coc/notes/ping", consumes = { APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE }, produces = { APPLICATION_JSON_VALUE,
        TEXT_PLAIN_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
@Tag(name = "Notes", description = "Notes API allows to enter notes for supported resources.")
@RequiredArgsConstructor
public class NotesPingController {

    @GetMapping
    public String ping(@RequestParam("message") String message) {
        // NOTE: just for quick verification that Spring Web is in place

        final var tenant = ThreadLocalContextUtil.getTenant();

        return String.format("PONG: %s from %s", message, tenant.getName());
    }
}
