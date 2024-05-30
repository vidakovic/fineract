package org.apache.fineract.portfolio.note.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class NoteResultData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long entityId;
    private Long clientId;
    private Long groupId;
    private Long loanId;
    private Long savingsAccountId;
    private Long officeId;
}
