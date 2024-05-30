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
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.note.mapping.NoteFacadeMapper;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.note.service.NoteReadPlatformService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoteReadPlaformFacadeService implements NoteReadPlatformService {

    private final NoteJdbcReadService noteJdbcReadService;

    private final NoteFacadeMapper noteFacadeMapper;

    @PostConstruct
    public void init() {
        log.warn("We have replaced the original {} with an implementation that uses native - type safe - SQL queries!",
                NoteReadPlatformService.class.getName());
    }

    @Override
    public NoteData retrieveNote(Long noteId, Long resourceId, Integer noteTypeId) {
        return noteFacadeMapper.map(noteJdbcReadService.retrieveNote(noteId, resourceId, noteTypeId));
    }

    @Override
    public Collection<NoteData> retrieveNotesByResource(Long resourceId, Integer noteTypeId) {
        return noteFacadeMapper.mapAll(noteJdbcReadService.retrieveNotesByResource(resourceId, noteTypeId));
    }
}
