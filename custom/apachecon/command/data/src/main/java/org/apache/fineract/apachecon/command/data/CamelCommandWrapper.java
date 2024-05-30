package org.apache.fineract.apachecon.command.data;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public final class CamelCommandWrapper implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long commandId;
    private Long officeId;
    private Long groupId;
    private Long clientId;
    private Long loanId;
    private Long savingsId;
    private String actionName;
    private String entityName;
    private String taskPermissionName;
    private Long entityId;
    private Long subentityId;
    private String href;
    private String json;
    private String transactionId;
    private Long productId;
    private Long creditBureauId;
    private Long organisationCreditBureauId;
    private String jobName;
    private String idempotencyKey;
    private Long templateId;
}
