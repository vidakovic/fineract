package org.apache.fineract.makerchecker.service;

import static org.apache.fineract.command.core.CommandState.REJECTED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.command.core.CommandContext;
import org.apache.fineract.command.core.CommandDispatcher;
import org.apache.fineract.command.core.CommandStore;
import org.apache.fineract.makerchecker.data.MakerCheckerApproveRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerApproveResponse;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnMissingBean(value = MakerCheckerReadService.class, ignored = MakerCheckerWriteServiceImpl.class)
public class MakerCheckerWriteServiceImpl implements MakerCheckerWriteService {

    private final CommandStore store;
    private final CommandDispatcher dispatcher;

    @Override
    public MakerCheckerApproveResponse approve(MakerCheckerApproveRequest request) {
        var command = store.<Command<Object>>getRequestById(request.getCommandId());

        if (command != null) {
            var response = dispatcher.dispatch(command);

            // NOTE: do we have to do anything with the response? There is no back-channel to the originating user
            response.get();

            return MakerCheckerApproveResponse.builder().build();
        }

        // TODO: app specific exception
        throw new RuntimeException("Command with ID %s not found!".formatted(request.getCommandId()));
    }

    @Override
    public MakerCheckerRejectResponse reject(MakerCheckerRejectRequest request) {
        var command = store.<Command<Object>>getRequestById(request.getCommandId());

        if (command != null) {
            var ctx = CommandContext.builder().command(command).state(REJECTED).build();

            store.store(ctx);

            return MakerCheckerRejectResponse.builder().build();
        }

        // TODO: app specific exception
        throw new RuntimeException("Command with ID %s not found!".formatted(request.getCommandId()));
    }
}
