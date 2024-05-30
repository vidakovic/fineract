package org.apache.fineract.apachecon.command.service;

import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apache.fineract.apachecon.command.data.CommandResponse;

public interface CommandDispatcher {
    CommandResponse<?> dispatch(CommandRequest<?> request);
}
