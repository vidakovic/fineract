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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.note.data.NoteJdbcData;
import org.apache.fineract.apachecon.note.service.NoteJdbcReadService;
import org.apache.fineract.portfolio.note.api.NotesApiResourceSwagger;
import org.apache.fineract.portfolio.note.domain.NoteType;
import org.apache.fineract.portfolio.note.exception.NoteResourceNotSupportedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
@RequestMapping(value = "/coc/{resourceType}/{resourceId}/notes", consumes = { APPLICATION_JSON_VALUE }, produces = {
        APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
@Tag(name = "Notes", description = "Notes API allows to enter notes for supported resources.")
@RequiredArgsConstructor
public class NotesApiController {
    // NOTE: only read requests here

    private final NoteJdbcReadService noteReadService;

    @GetMapping
    @Operation(summary = "Retrieve a Resource's description", description = "Retrieves a Resource's Notes\n\n"
            + "Note: Notes are returned in descending createOn order.\n" + "\n" + "Example Requests:\n" + "\n" + "clients/2/notes\n" + "\n"
            + "\n" + "groups/2/notes?fields=note,createdOn,createdByUsername")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotesApiResourceSwagger.GetResourceTypeResourceIdNotesResponse.class)))) })
    public Collection<NoteJdbcData> retrieveNotesByResource(@PathVariable("resourceType") final String resourceType,
            @PathVariable("resourceId") final Long resourceId) {

        log.warn(">>>>>>>>>>>>>>>>>> Community over Code 2024: Note Service - 'retrieveNotesByResource'");

        final NoteType noteType = NoteType.fromApiUrl(resourceType);

        if (noteType == null) {
            throw new NoteResourceNotSupportedException(resourceType);
        }

        // TODO: define this in security configuration
        // this.context.authenticatedUser().validateHasReadPermission(getResourceDetails(noteType,
        // resourceId).entityName());

        final Integer noteTypeId = noteType.getValue();

        return this.noteReadService.retrieveNotesByResource(resourceId, noteTypeId);
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "Retrieve a Resource Note", description = "Retrieves a Resource Note\n\n" + "Example Requests:\n" + "\n"
            + "clients/1/notes/76\n" + "\n" + "\n" + "groups/1/notes/20\n" + "\n" + "\n"
            + "clients/1/notes/76?fields=note,createdOn,createdByUsername\n" + "\n" + "\n"
            + "groups/1/notes/20?fields=note,createdOn,createdByUsername")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = NotesApiResourceSwagger.GetResourceTypeResourceIdNotesNoteIdResponse.class))) })
    public NoteJdbcData retrieveNote(@PathVariable("resourceType") final String resourceType,
            @PathVariable("resourceId") final Long resourceId, @PathVariable("noteId") final Long noteId,
            @RequestParam(value = "prettyPrint", defaultValue = "true") Boolean prettyPrint,
            @RequestParam(value = "template", defaultValue = "false") Boolean template,
            @RequestParam(value = "makerCheckerable", defaultValue = "false") Boolean makerCheckerable,
            @RequestParam(value = "includeJson", defaultValue = "true") Boolean includeJson) {

        log.warn(">>>>>>>>>>>>>>>>>> Community over Code 2024: Note Service - 'retrieveNote'");

        final NoteType noteType = NoteType.fromApiUrl(resourceType);

        if (noteType == null) {
            throw new NoteResourceNotSupportedException(resourceType);
        }

        // TODO: define this in security configuration
        // this.context.authenticatedUser().validateHasReadPermission(getResourceDetails(noteType,
        // resourceId).entityName());

        return this.noteReadService.retrieveNote(noteId, resourceId, noteType.getValue());
    }
}
