package org.apache.fineract.makerchecker.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class MakerCheckerRejectCommand extends Command<MakerCheckerRejectRequest> {}
