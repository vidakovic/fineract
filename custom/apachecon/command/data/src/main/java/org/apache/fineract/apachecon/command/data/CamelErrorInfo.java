package org.apache.fineract.apachecon.command.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
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
public final class CamelErrorInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer statusCode;
    private Integer errorCode;
    private String message;
    private Set<CamelErrorInfoHeader> headers;
}
