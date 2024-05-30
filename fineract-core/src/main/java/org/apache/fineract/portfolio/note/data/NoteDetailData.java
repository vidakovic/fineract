package org.apache.fineract.portfolio.note.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class NoteDetailData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String type;
    @NotEmpty
    private String note;
    @NotNull
    private Long resourceId;
}
