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
package org.apache.fineract.portfolio.note.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.group.exception.GroupNotFoundException;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.apache.fineract.portfolio.note.data.NoteDetailData;
import org.apache.fineract.portfolio.note.data.NoteResultData;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.note.domain.NoteType;
import org.apache.fineract.portfolio.note.exception.NoteNotFoundException;
import org.apache.fineract.portfolio.note.exception.NoteResourceNotSupportedException;
import org.apache.fineract.portfolio.note.serialization.NoteCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class NoteWritePlatformServiceJpaRepositoryImpl implements NoteWritePlatformService {

    private final NoteRepository noteRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final GroupRepository groupRepository;
    private final LoanRepositoryWrapper loanRepository;
    private final LoanTransactionRepository loanTransactionRepository;
    private final NoteCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final SavingsAccountRepository savingsAccountRepository;

    @Override
    public NoteResultData createNote(final NoteDetailData noteDetail) {
        var note = new Note();
        note.setNote(noteDetail.getNote());

        var result = new NoteResultData();

        switch (noteDetail.getType()) {
            case "CLIENT":
                note.setNoteTypeId(NoteType.CLIENT.getValue());
                note.setClient(clientRepository.findOneWithNotFoundDetection(noteDetail.getResourceId()));
                result.setClientId(note.getClient().getId());
                result.setOfficeId(note.getClient().getOffice().getId());
                break;
            case "GROUP":
                note.setNoteTypeId(NoteType.GROUP.getValue());
                note.setGroup(groupRepository.findById(noteDetail.getResourceId()).orElseThrow(() -> new GroupNotFoundException(noteDetail.getResourceId())));
                result.setGroupId(note.getGroup().getId());
                result.setOfficeId(note.getClient().getOffice().getId());
                break;
            case "LOAN":
                note.setNoteTypeId(NoteType.LOAN.getValue());
                note.setLoan(loanRepository.findOneWithNotFoundDetection(noteDetail.getResourceId()));
                result.setLoanId(note.getLoan().getId());
                result.setOfficeId(note.getLoan().getClient().getOffice().getId());
                break;
            case "LOAN_TRANSACTION":
                note.setNoteTypeId(NoteType.LOAN_TRANSACTION.getValue());
                note.setLoanTransaction(loanTransactionRepository.findById(noteDetail.getResourceId()).orElseThrow(() -> new LoanTransactionNotFoundException(noteDetail.getResourceId())));
                note.setLoan(note.getLoanTransaction().getLoan());
                result.setLoanId(note.getLoan().getId());
                result.setOfficeId(note.getLoan().getClient().getOffice().getId());
                break;
            case "SAVING_ACCOUNT":
                note.setNoteTypeId(NoteType.SAVING_ACCOUNT.getValue());
                note.setSavingsAccount(savingsAccountRepository.findById(noteDetail.getResourceId()).orElseThrow(() -> new SavingsAccountNotFoundException(noteDetail.getResourceId())));
                result.setSavingsAccountId(note.getSavingsAccount().getId());
                result.setOfficeId(note.getSavingsAccount().getClient().getOffice().getId());
                break;
            default:
                throw new NoteResourceNotSupportedException(noteDetail.getType());
        }

        note = noteRepository.saveAndFlush(note);

        result.setEntityId(note.getId());

        return result;
    }

    @Override
    public NoteResultData updateNote(final NoteDetailData noteDetail) {
        Note note = noteRepository.findById(noteDetail.getResourceId()).orElseThrow(() -> new NoteNotFoundException(noteDetail.getResourceId()));
        note.setNote(noteDetail.getNote());

        var result = new NoteResultData();
        result.setEntityId(note.getId());

        switch (noteDetail.getType()) {
            case "CLIENT":
                result.setClientId(note.getClient().getId());
                result.setOfficeId(note.getClient().getOffice().getId());
                break;
            case "GROUP":
                result.setGroupId(note.getGroup().getId());
                result.setOfficeId(note.getClient().getOffice().getId());
                break;
            case "LOAN":
                result.setLoanId(note.getLoan().getId());
                result.setOfficeId(note.getLoan().getClient().getOffice().getId());
                break;
            case "LOAN_TRANSACTION":
                result.setLoanId(note.getLoan().getId());
                result.setOfficeId(note.getLoan().getClient().getOffice().getId());
                break;
            case "SAVING_ACCOUNT":
                result.setSavingsAccountId(note.getSavingsAccount().getId());
                result.setOfficeId(note.getSavingsAccount().getClient().getOffice().getId());
                break;
            default:
                throw new NoteResourceNotSupportedException(noteDetail.getType());
        }

        return result;
    }

    @Override
    public NoteResultData deleteNote(final NoteDetailData noteDetail) {

        noteRepository.deleteById(noteDetail.getResourceId());

        var result = new NoteResultData();
        result.setEntityId(noteDetail.getResourceId());

        return result;
    }
}
