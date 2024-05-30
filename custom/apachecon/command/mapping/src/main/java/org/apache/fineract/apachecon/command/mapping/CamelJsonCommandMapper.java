package org.apache.fineract.apachecon.command.mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.apache.fineract.apachecon.command.data.CamelJsonCommand;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public abstract class CamelJsonCommandMapper {

    @Autowired
    protected FromJsonHelper fromJsonHelper;

    @Autowired
    protected GsonMapper gsonMapper;

    @Mapping(target = "jsonCommand", expression = "java( source.json() )")
    @Mapping(target = "parsedCommand", expression = "java( gsonMapper.map(source.parsedJson()) )")
    @Mapping(target = "commandId", expression = "java( source.commandId() )")
    @Mapping(target = "resourceId", expression = "java( source.entityId() )")
    @Mapping(target = "subresourceId", expression = "java( source.subentityId() )")
    @Mapping(target = "entityName", expression = "java( source.entityName() )")
    public abstract CamelJsonCommand map(JsonCommand source);

    public JsonCommand map(CamelJsonCommand source) {
        if (source == null) {
            return null;
        }

        return JsonCommand.fromExistingCommand(source.getCommandId(), source.getJsonCommand(), gsonMapper.map(source.getParsedCommand()),
                fromJsonHelper, source.getEntityName(), source.getResourceId(), source.getSubresourceId(), source.getGroupId(),
                source.getClientId(), source.getLoanId(), source.getSavingsId(), source.getTransactionId(), source.getUrl(),
                source.getProductId(), source.getCreditBureauId(), source.getOrganisationCreditBureauId(), source.getJobName());
    }
}
