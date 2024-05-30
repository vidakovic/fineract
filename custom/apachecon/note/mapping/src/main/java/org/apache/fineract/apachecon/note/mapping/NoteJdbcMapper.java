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
package org.apache.fineract.apachecon.note.mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import java.util.List;
import org.apache.fineract.apachecon.note.data.NoteJdbcData;
import org.apache.fineract.apachecon.note.domain.NoteJdbc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS, uses = { NoteEnumerationMapper.class, DateMapper.class })
public interface NoteJdbcMapper {

    @Mapping(source = "createdBy", target = "createdById")
    @Mapping(source = "lastModifiedBy", target = "updatedById")
    @Mapping(source = "notes", target = "note")
    @Mapping(source = "createdDate", target = "createdOn")
    @Mapping(source = "lastModifiedDate", target = "updatedOn")
    @Mapping(source = "savingsAccountId", target = "savingAccountId")
    // @Mapping(source = "savingsTransactionId", target = "")
    NoteJdbcData map(NoteJdbc source);

    List<NoteJdbcData> mapAll(List<NoteJdbc> sources);
}
