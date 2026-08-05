package org.apache.fineract.makerchecker.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakerCheckerListRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Instant from;
    private Instant to;
    private String type;
}
