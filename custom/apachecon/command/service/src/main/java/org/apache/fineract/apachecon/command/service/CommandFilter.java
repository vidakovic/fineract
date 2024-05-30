package org.apache.fineract.apachecon.command.service;

import org.apache.fineract.apachecon.command.data.CommandRequest;

public interface CommandFilter {
    boolean filter(CommandRequest<?> request);
}
