package org.apache.fineract.apachecon.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultCommandIdempotentFilter implements CommandFilter {

    private final static Set<UUID> REQUEST_IDS = new HashSet<>();

    @Override
    public boolean filter(CommandRequest<?> request) {
        // TODO: more sophisticated implementation, possibly with distributed SET/MAP (Redis)

        boolean pass = !REQUEST_IDS.contains(request.getId());

        REQUEST_IDS.add(request.getId());

        return pass;
    }
}
