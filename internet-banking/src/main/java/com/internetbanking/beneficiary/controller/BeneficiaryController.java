package com.internetbanking.beneficiary.controller;

import com.internetbanking.beneficiary.dto.BeneficiaryRequestDTO;
import com.internetbanking.beneficiary.dto.BeneficiaryResponseDTO;
import com.internetbanking.beneficiary.service.BeneficiaryService;
import com.internetbanking.common.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;
    private final AuthUtil authUtil;

    public BeneficiaryController(BeneficiaryService beneficiaryService, AuthUtil authUtil) {
        this.beneficiaryService = beneficiaryService;
        this.authUtil = authUtil;
    }

    @PostMapping("/add")
    public ResponseEntity<BeneficiaryResponseDTO> addBeneficiary(@Valid @RequestBody BeneficiaryRequestDTO requestDTO) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        return ResponseEntity.ok(beneficiaryService.addBeneficiary(requestDTO, loggedInEmail));
    }

    @GetMapping("/user")
    public ResponseEntity<List<BeneficiaryResponseDTO>> getUserBeneficiaries() {
        String loggedInEmail = authUtil.getLoggedInEmail();
        return ResponseEntity.ok(beneficiaryService.getUserBeneficiaries(loggedInEmail));
    }

    @PutMapping("/{beneficiaryId}")
    public ResponseEntity<BeneficiaryResponseDTO> updateBeneficiary(@PathVariable Long beneficiaryId,
                                                                    @Valid @RequestBody BeneficiaryRequestDTO request) {

        String loggedInEmail = authUtil.getLoggedInEmail();

        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(beneficiaryId, request, loggedInEmail));
    }

    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long beneficiaryId) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        beneficiaryService.deleteBeneficiary(beneficiaryId, loggedInEmail);
        return ResponseEntity.noContent().build();
    }
}