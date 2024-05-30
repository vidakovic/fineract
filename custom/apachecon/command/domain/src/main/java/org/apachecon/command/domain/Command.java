package org.apachecon.command.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table("m_command")
public class Command {
    @Id
    private Long id;
    private UUID requestId;
    private String fqn;
    private String tenantId;
    private Long userId;
    private Boolean approvalRequired;
    private Boolean approvalStatus;
    private Long approverId;
    private Instant createdAt;
    private Instant approvedAt;
    private Instant processedAt;
    private JsonNode body;
}
