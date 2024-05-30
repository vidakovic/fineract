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
public final class CamelJsonCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String jsonCommand;
    private String parsedCommand;
    private Long commandId;
    private Long resourceId;
    private Long subresourceId;
    private Long groupId;
    private Long clientId;
    private Long loanId;
    private Long savingsId;
    private String entityName;
    private String transactionId;
    private String url;
    private Long productId;
    private Long creditBureauId;
    private Long organisationCreditBureauId;
    private String jobName;
}
