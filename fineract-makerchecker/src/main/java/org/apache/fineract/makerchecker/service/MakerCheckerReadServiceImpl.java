package org.apache.fineract.makerchecker.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.makerchecker.data.MakerCheckerListRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnMissingBean(value = MakerCheckerReadService.class, ignored = MakerCheckerReadServiceImpl.class)
public class MakerCheckerReadServiceImpl implements MakerCheckerReadService {

    @Override
    public List<Command<?>> listCommands(MakerCheckerListRequest request) {
        return List.of();
    }
}
