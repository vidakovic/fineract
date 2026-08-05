package org.apache.fineract.makerchecker.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.command.core.CommandDispatcher;
import org.apache.fineract.makerchecker.command.MakerCheckerApproveCommand;
import org.apache.fineract.makerchecker.command.MakerCheckerRejectCommand;
import org.apache.fineract.makerchecker.data.MakerCheckerApproveRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerApproveResponse;
import org.apache.fineract.makerchecker.data.MakerCheckerListRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectResponse;
import org.apache.fineract.makerchecker.service.MakerCheckerReadService;
import org.apache.fineract.makerchecker.service.MakerCheckerWriteService;
import org.springframework.stereotype.Component;

@Path("/v1/makerchecker")
@Component
@Tag(name = "Maker Checker")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@RequiredArgsConstructor
class MakerCheckerApiResource {

    private final MakerCheckerReadService makerCheckerReadService;
    private final MakerCheckerWriteService makerCheckerWriteService;
    private final CommandDispatcher dispatcher;

    @GET
    @Operation(summary = "List pending commands")
    List<Command<?>> listCommands() {
        // TODO: implement this
        var request = MakerCheckerListRequest.builder().build();

        return makerCheckerReadService.listCommands(request);
    }

    @POST
    @Path("approve")
    @Operation(summary = "Approve command execution")
    MakerCheckerApproveResponse approve(MakerCheckerApproveRequest request) {
        var command = new MakerCheckerApproveCommand();

        command.setPayload(request);

        final Supplier<MakerCheckerApproveResponse> response = dispatcher.dispatch(command);

        return response.get();
    }

    @POST
    @Path("reject")
    @Operation(summary = "Reject command execution")
    MakerCheckerRejectResponse reject(MakerCheckerRejectRequest request) {
        var command = new MakerCheckerRejectCommand();

        command.setPayload(request);

        final Supplier<MakerCheckerRejectResponse> response = dispatcher.dispatch(command);

        return response.get();
    }
}
