package org.apache.fineract.apachecon.command.mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.apache.fineract.apachecon.command.data.CamelCommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public abstract class CamelCommandProcessingResultMapper {

    public abstract CamelCommandProcessingResult map(CommandProcessingResult source);

    public CommandProcessingResult map(CamelCommandProcessingResult source) {
        if (source == null) {
            return null;
        }

        return CommandProcessingResult.fromDetails(source.getCommandId(), source.getOfficeId(), source.getGroupId(), source.getClientId(),
                source.getLoanId(), source.getSavingsId(), source.getResourceIdentifier(), source.getResourceId(), source.getGsimId(),
                source.getGlimId(), source.getCreditBureauReportData(), source.getTransactionId(), source.getChanges(),
                source.getProductId(), source.getRollbackTransaction(), source.getSubResourceId(), source.getResourceExternalId(),
                source.getSubResourceExternalId());
    }
}
