/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.collateralmanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.portfolio.collateralmanagement.data.ClientCollateralManagementData;
import org.apache.fineract.portfolio.collateralmanagement.data.LoanCollateralTemplateData;
import org.apache.fineract.portfolio.collateralmanagement.domain.ClientCollateralManagement;
import org.apache.fineract.portfolio.collateralmanagement.service.ClientCollateralManagementReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/clients/{clientId}/collaterals")
@Component
@Tag(name = "Client Collateral Management", description = "Client Collateral Management is for managing collateral operations")
@RequiredArgsConstructor
public class ClientCollateralManagementApiResource {

    private final DefaultToApiJsonSerializer<ClientCollateralManagement> apiJsonSerializerService;
    private final DefaultToApiJsonSerializer<ClientCollateralManagementData> apiJsonSerializerDataService;
    private final DefaultToApiJsonSerializer<LoanCollateralTemplateData> apiJsonSerializerForLoanCollateralTemplateService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ClientCollateralManagementReadPlatformService clientCollateralManagementReadPlatformService;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Get Clients Collateral Products", description = "Get Collateral Product of a Client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.GetClientCollateralManagementsResponse.class)))) })
    public String getClientCollateral(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @QueryParam("prodId") @Parameter(description = "prodId") final Long prodId) {
        // TODO: @vidakovic check permission CLIENT_COLLATERAL_PRODUCT
        List<ClientCollateralManagementData> collateralProductList = this.clientCollateralManagementReadPlatformService
                .getClientCollaterals(clientId, prodId);

        return this.apiJsonSerializerDataService.serialize(collateralProductList);
    }

    @GET
    @Path("{clientCollateralId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Get Client Collateral Data", description = "Get Client Collateral Data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.GetClientCollateralManagementsResponse.class))) })
    public String getClientCollateralData(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @PathParam("clientCollateralId") @Parameter(description = "clientCollateralId") final Long collateralId) {
        // TODO: @vidakovic check permission CLIENT_COLLATERAL_PRODUCT
        ClientCollateralManagementData clientCollateralManagementData = this.clientCollateralManagementReadPlatformService
                .getClientCollateralManagementData(collateralId);

        return this.apiJsonSerializerDataService.serialize(clientCollateralManagementData);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Get Client Collateral Template", description = "Get Client Collateral Template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.GetLoanCollateralManagementTemplate.class)))) })
    public String getClientCollateralTemplate(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId) {
        List<LoanCollateralTemplateData> loanCollateralTemplateDataList = this.clientCollateralManagementReadPlatformService
                .getLoanCollateralTemplate(clientId);
        return this.apiJsonSerializerForLoanCollateralTemplateService.serialize(loanCollateralTemplateDataList);
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Add New Collateral For a Client", description = "Add New Collateral For a Client")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.PostClientCollateralRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.PostClientCollateralResponse.class))) })
    public String addCollateral(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @Parameter(hidden = true) String apiJsonRequestBody) {
        final CommandWrapper commandWrapper = new CommandWrapperBuilder().addClientCollateralProduct(clientId).withJson(apiJsonRequestBody)
                .build();

        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);

        return this.apiJsonSerializerService.serialize(commandProcessingResult);
    }

    @PUT
    @Path("{collateralId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update New Collateral of a Client", description = "Update New Collateral of a Client")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.PutClientCollateralRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.PutClientCollateralResponse.class))) })
    public String updateCollateral(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @PathParam("collateralId") @Parameter(description = "collateralId") final Long collateralId,
            @Parameter(hidden = true) String apiJsonRequestBody) {

        final CommandWrapper commandWrapper = new CommandWrapperBuilder().updateClientCollateralProduct(clientId, collateralId)
                .withJson(apiJsonRequestBody).build();

        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);

        return this.apiJsonSerializerService.serialize(commandProcessingResult);
    }

    @DELETE
    @Path("{collateralId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Delete Client Collateral", description = "Delete Client Collateral")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientCollateralManagementApiResourceSwagger.DeleteClientCollateralResponse.class))) })
    public String deleteCollateral(@PathParam("clientId") @Parameter(description = "clientId") final Long clientId,
            @PathParam("collateralId") @Parameter(description = "collateralId") final Long collateralId) {
        final CommandWrapper commandWrapper = new CommandWrapperBuilder().deleteClientCollateralProduct(collateralId, clientId).build();
        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
        return this.apiJsonSerializerService.serialize(commandProcessingResult);

    }
}
