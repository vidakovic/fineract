package org.apache.fineract.apachecon.command.service;

import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apache.fineract.apachecon.command.data.CommandResponse;

public interface CommandHandler<I extends CommandRequest<?>, O extends CommandResponse<?>> {
    boolean accept(Class<? extends CommandRequest> clazz);

    O handle(I request);
}
