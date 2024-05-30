package org.apache.fineract.apachecon.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apache.fineract.apachecon.command.data.CommandResponse;
import org.apache.fineract.apachecon.command.mapping.CommandMapper;
import org.apachecon.command.domain.CommandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultCommandProcessor implements CommandProcessor {

    private final List<CommandHandler<CommandRequest<?>, CommandResponse<?>>> handlers;

    private final CommandRepository commandRepository;

    private final CommandMapper commandMapper;

    @Override
    public CommandResponse<?> process(CommandRequest<?> request) {
        for (var handler : handlers) {
            if(handler.accept(request.getClass())) {
                commandRepository.save(commandMapper.map(request));

                return handler.handle(request);
            }
        }

        // TODO: throw exception?!?
        throw new RuntimeException("No command handler for: " + request.getClass().getCanonicalName());
    }
}
