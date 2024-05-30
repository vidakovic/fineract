package org.apache.fineract.apachecon.command.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.fineract.infrastructure.core.domain.ExternalId;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public final class CamelCommandProcessingResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long commandId;
    private Long officeId;
    private Long groupId;
    private Long clientId;
    private Long loanId;
    private Long savingsId;
    private Long resourceId;
    private Long subResourceId;
    private String transactionId;
    private Map<String, Object> changes;
    private Map<String, Object> creditBureauReportData;
    private String resourceIdentifier;
    private Long productId;
    private Long gsimId;
    private Long glimId;
    private Boolean rollbackTransaction;
    private ExternalId resourceExternalId;
    private ExternalId subResourceExternalId;
}
