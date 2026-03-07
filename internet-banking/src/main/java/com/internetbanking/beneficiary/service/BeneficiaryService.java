package com.internetbanking.beneficiary.service;

import com.internetbanking.beneficiary.dto.BeneficiaryRequestDTO;
import com.internetbanking.beneficiary.dto.BeneficiaryResponseDTO;
import java.util.List;

public interface BeneficiaryService {
    BeneficiaryResponseDTO addBeneficiary(BeneficiaryRequestDTO requestDTO, String loggedInEmail);
    List<BeneficiaryResponseDTO> getUserBeneficiaries(String loggedInEmail);
    BeneficiaryResponseDTO updateBeneficiary(Long beneficiaryId, BeneficiaryRequestDTO request, String loggedInEmail);
    void deleteBeneficiary(Long beneficiaryId, String loggedInEmail);
}