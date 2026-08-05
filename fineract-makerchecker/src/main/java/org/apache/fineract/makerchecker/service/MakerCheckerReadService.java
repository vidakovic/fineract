package org.apache.fineract.makerchecker.service;

import java.util.List;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.makerchecker.data.MakerCheckerListRequest;

public interface MakerCheckerReadService {

    List<Command<?>> listCommands(MakerCheckerListRequest request);
}
