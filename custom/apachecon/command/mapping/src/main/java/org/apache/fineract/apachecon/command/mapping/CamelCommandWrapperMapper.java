package org.apache.fineract.apachecon.command.mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.apache.fineract.apachecon.command.data.CamelCommandWrapper;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public interface CamelCommandWrapperMapper {

    CamelCommandWrapper map(CommandWrapper source);

    CommandWrapper map(CamelCommandWrapper source);
}
