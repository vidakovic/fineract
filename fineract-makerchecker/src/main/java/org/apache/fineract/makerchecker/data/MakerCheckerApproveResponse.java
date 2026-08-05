package org.apache.fineract.makerchecker.data;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakerCheckerApproveResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
