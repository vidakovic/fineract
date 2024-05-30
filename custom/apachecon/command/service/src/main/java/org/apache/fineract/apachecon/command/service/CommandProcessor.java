package org.apache.fineract.apachecon.command.service;

import org.apache.fineract.apachecon.command.data.CommandRequest;
import org.apache.fineract.apachecon.command.data.CommandResponse;

public interface CommandProcessor {
    CommandResponse<?> process(CommandRequest<?> request);
}
