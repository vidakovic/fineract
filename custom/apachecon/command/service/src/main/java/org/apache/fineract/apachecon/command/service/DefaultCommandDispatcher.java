package org.apache.fineract.apachecon.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apache.fineract.apachecon.command.data.CommandResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultCommandDispatcher implements CommandDispatcher {

    private final DefaultCommandProcessor defaultCommandProcessor;

    private final DefaultCommandIdempotentFilter idempotentFilter;

    private final ApplicationEventPublisher publisher;

    @Override
    public CommandResponse<?> dispatch(CommandRequest<?> request) {

        if(!idempotentFilter.filter(request)) {
            // TODO: throw proper platform exception
            throw new RuntimeException("Duplicate request: " + request.getId());
        }

        // TODO: eventually add more processors/filters if needed until we match the current implementation
        request.setId(UUID.randomUUID());
        request.setCreatedAt(Instant.now());

        // TODO: create event object and do not send the entire request
        publisher.publishEvent(request);

        return defaultCommandProcessor.process(request);
    }
}
