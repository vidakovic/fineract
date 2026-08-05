package org.apache.fineract.makerchecker.service;

import org.apache.fineract.makerchecker.data.MakerCheckerApproveRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerApproveResponse;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectRequest;
import org.apache.fineract.makerchecker.data.MakerCheckerRejectResponse;

public interface MakerCheckerWriteService {

    MakerCheckerApproveResponse approve(MakerCheckerApproveRequest request);

    MakerCheckerRejectResponse reject(MakerCheckerRejectRequest request);
}
