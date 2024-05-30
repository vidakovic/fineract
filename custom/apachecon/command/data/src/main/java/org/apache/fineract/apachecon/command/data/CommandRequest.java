package org.apache.fineract.apachecon.command.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonPropertyOrder(alphabetic=true)
public class CommandRequest<T> implements Serializable {
    private UUID id;
    private String tenantId;
    private Long userId;
    private Boolean approvalRequired;
    private Boolean approvalStatus;
    private Long approverId;
    private Instant createdAt;
    private Instant approvedAt;
    private Instant processedAt;
    private T body;
}
