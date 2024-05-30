package org.apache.fineract.apachecon.command.mapping;

import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apachecon.command.domain.Command;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public interface CommandMapper {
    Command map(CommandRequest<?> source);
}
