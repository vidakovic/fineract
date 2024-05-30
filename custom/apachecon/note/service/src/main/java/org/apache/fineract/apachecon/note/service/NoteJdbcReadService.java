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

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.annotation.PostConstruct;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.note.data.NoteJdbcData;
import org.apache.fineract.apachecon.note.domain.NoteJdbcRepository;
import org.apache.fineract.apachecon.note.domain.NoteType;
import org.apache.fineract.apachecon.note.domain.QNoteJdbc;
import org.apache.fineract.apachecon.note.mapping.NoteJdbcMapper;
import org.apache.fineract.portfolio.note.exception.NoteNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoteJdbcReadService /* implements NoteReadPlatformService */ {

    private final NoteJdbcRepository noteJdbcRepository;

    private final NoteJdbcMapper noteJdbcMapper;

    private final AppUserJdbcReadService appUserJdbcReadService;

    @PostConstruct
    public void init() {
        log.warn(">>>>>>>>>>>>>>>>>> Community over Code 2024: Note Service!!!");
    }

    // NOTE: we know this is used only once, so let's create something similar instead of being tied to the original
    // implementation
    // @Override
    public NoteJdbcData retrieveNote(Long noteId, Long resourceId, Integer noteTypeId) {
        final NoteType noteType = NoteType.fromInt(noteTypeId);

        var note$ = QNoteJdbc.noteJdbc;

        var criteria = addResourceCriteria(note$.id.eq(noteId), noteType, resourceId);

        log.warn("Note query: {}", criteria);

        var result = noteJdbcRepository.queryOne(
                query -> query.select(noteJdbcRepository.entityProjection()).from(note$).where(criteria).orderBy(note$.createdBy.desc()));

        if (result.isPresent()) {
            var noteData = noteJdbcMapper.map(result.get());

            var createdBy = appUserJdbcReadService.retrieveUser(noteData.getCreatedById());
            var modifiedBy = appUserJdbcReadService.retrieveUser(noteData.getUpdatedById());

            if (createdBy != null) {
                noteData.setCreatedByUsername(createdBy.getUsername());
            }
            if (modifiedBy != null) {
                noteData.setCreatedByUsername(modifiedBy.getUsername());
            }

            return noteData;
        }

        throw new NoteNotFoundException(noteId, resourceId, noteType.name().toLowerCase());
    }

    // @Override
    public Collection<NoteJdbcData> retrieveNotesByResource(Long resourceId, Integer noteTypeId) {
        final var noteType = NoteType.fromInt(noteTypeId);

        var note$ = QNoteJdbc.noteJdbc;

        var criteria = addResourceCriteria(note$.id.gt(0), noteType, resourceId);

        log.warn("Note query: {}", criteria);

        return noteJdbcMapper.mapAll(noteJdbcRepository.queryMany(
                query -> query.select(noteJdbcRepository.entityProjection()).from(note$).where(criteria).orderBy(note$.createdBy.desc())));
    }

    public BooleanExpression addResourceCriteria(BooleanExpression criteria, NoteType noteType, Long resourceId) {
        var note$ = QNoteJdbc.noteJdbc;

        switch (noteType) {
            case CLIENT:
                log.warn("... filter client data... {} - {}", resourceId, noteType.getValue());
                return criteria.and(note$.clientId.eq(resourceId)).and(note$.noteTypeId.eq(NoteType.CLIENT.getValue()));
            case LOAN:
                return criteria.and(note$.loanId.eq(resourceId))
                        .and(note$.noteTypeId.eq(NoteType.LOAN.getValue()).or(note$.noteTypeId.eq(NoteType.LOAN_TRANSACTION.getValue())));
            case LOAN_TRANSACTION:
                return criteria.and(note$.loanTransactionId.eq(resourceId));
            case SAVING_ACCOUNT:
                return criteria.and(note$.savingsAccountId.eq(resourceId)).and(note$.noteTypeId.eq(NoteType.SAVING_ACCOUNT.getValue())
                        .or(note$.noteTypeId.eq(NoteType.SAVINGS_TRANSACTION.getValue())));
            case SAVINGS_TRANSACTION:
                return criteria.and(note$.savingsTransactionId.eq(resourceId));
            case GROUP:
                return criteria.and(note$.groupId.eq(resourceId));
            default:
                return criteria;
        }
    }
}
