package org.apache.fineract.apachecon.command.mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.apache.fineract.apachecon.command.data.CamelErrorInfoHeader;
import org.apache.fineract.batch.domain.Header;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public interface CamelErrorInfoHeaderMapper {

    CamelErrorInfoHeader map(Header source);

    Header map(CamelErrorInfoHeader source);
}
