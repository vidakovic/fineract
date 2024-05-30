package org.apache.fineract.apachecon.command.mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.apache.fineract.apachecon.command.data.CamelErrorInfo;
import org.apache.fineract.batch.exception.ErrorInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS, uses = { CamelErrorInfoHeaderMapper.class })
public interface CamelErrorInfoMapper {

    CamelErrorInfo map(ErrorInfo source);

    ErrorInfo map(CamelErrorInfo source);
}
