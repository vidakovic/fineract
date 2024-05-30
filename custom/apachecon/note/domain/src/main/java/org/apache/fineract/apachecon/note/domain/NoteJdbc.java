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
package org.apache.fineract.apachecon.note.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table(name = "m_note")
public class NoteJdbc implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("created_by")
    private Long createdBy;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("last_modified_by")
    private Long lastModifiedBy;

    @Column("lastmodified_date")
    private LocalDateTime lastModifiedDate;

    @Column("client_id")
    private Long clientId;

    @Column("group_id")
    private Long groupId;

    @Column("loan_id")
    private Long loanId;

    @Column("loan_transaction_id")
    private Long loanTransactionId;

    @Column("savings_account_id")
    private Long savingsAccountId;

    @Column("savings_account_transaction_id")
    private Long savingsTransactionId;

    @Column("share_account_id")
    private Long shareAccountId;

    @Column("note")
    private String notes;

    @Column("note_type_enum")
    private Integer noteTypeId;
}
