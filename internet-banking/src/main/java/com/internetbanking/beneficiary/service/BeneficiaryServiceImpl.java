package com.internetbanking.beneficiary.service;

import com.internetbanking.transaction.dto.AccountLookupDTO;
import com.internetbanking.account.service.AccountReadPlatformService;
import com.internetbanking.beneficiary.dto.BeneficiaryRequestDTO;
import com.internetbanking.beneficiary.dto.BeneficiaryResponseDTO;
import com.internetbanking.beneficiary.entity.Beneficiary;
import com.internetbanking.beneficiary.exception.BeneficiaryAlreadyExistsException;
import com.internetbanking.beneficiary.repository.BeneficiaryRepository;
import com.internetbanking.common.exception.UnauthorizedException;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;
    private final AccountReadPlatformService accountReadPlatformService;

    public BeneficiaryServiceImpl(BeneficiaryRepository beneficiaryRepository,
                                   UserRepository userRepository,
                                   AccountReadPlatformService accountReadPlatformService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
        this.accountReadPlatformService = accountReadPlatformService;
    }

    @Override
    public BeneficiaryResponseDTO addBeneficiary(BeneficiaryRequestDTO requestDTO,
                                                   String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isOwnAccount = accountReadPlatformService
                .getAccountsByUserId(loggedInUser.getId(), loggedInEmail)
                .stream()
                .anyMatch(a -> a.getAccountNumber().equals(requestDTO.getAccountNumber()));

        if (isOwnAccount) {
            throw new RuntimeException("You cannot add your own account as a beneficiary");
        }

        if (beneficiaryRepository.existsByUserAndAccountNumber(loggedInUser, requestDTO.getAccountNumber())) {
            throw new BeneficiaryAlreadyExistsException(
                    "Beneficiary already exists for account: " + requestDTO.getAccountNumber());
        }

        AccountLookupDTO lookup = accountReadPlatformService.lookupAccount(requestDTO.getAccountNumber());

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(loggedInUser);
        beneficiary.setAccountNumber(requestDTO.getAccountNumber());
        beneficiary.setAccountHolderName(lookup.getAccountHolderName());
        beneficiary.setBankName("Secure Bank");
        beneficiary.setNickName(requestDTO.getNickName());

        return mapToDTO(beneficiaryRepository.save(beneficiary));
    }

    @Override
    public List<BeneficiaryResponseDTO> getUserBeneficiaries(String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return beneficiaryRepository.findByUserOrderByCreatedAtDesc(loggedInUser)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public BeneficiaryResponseDTO updateBeneficiary(Long beneficiaryId, BeneficiaryRequestDTO request, String loggedInEmail) {

        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(beneficiaryId, loggedInUser)
                .orElseThrow(() -> new UnauthorizedException("Beneficiary not found or does not belong to you"));

        beneficiary.setNickName(request.getNickName());

        if (request.getAccountNumber() != null && !request.getAccountNumber().equals(beneficiary.getAccountNumber())) {

            boolean isOwnAccount = accountReadPlatformService
                    .getAccountsByUserId(loggedInUser.getId(), loggedInEmail)
                    .stream()
                    .anyMatch(a -> a.getAccountNumber()
                            .equals(request.getAccountNumber()));

            if (isOwnAccount) {
                throw new RuntimeException("You cannot add your own account as a beneficiary");
            }

            if (beneficiaryRepository.existsByUserAndAccountNumber(loggedInUser, request.getAccountNumber())) {

                throw new BeneficiaryAlreadyExistsException("Beneficiary already exists for account: " + request.getAccountNumber());
            }

            AccountLookupDTO lookup = accountReadPlatformService.lookupAccount(request.getAccountNumber());

            beneficiary.setAccountNumber(request.getAccountNumber());
            beneficiary.setAccountHolderName(lookup.getAccountHolderName());
        }

        return mapToDTO(beneficiaryRepository.save(beneficiary));
    }

    @Override
    public void deleteBeneficiary(Long beneficiaryId, String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(beneficiaryId, loggedInUser)
                .orElseThrow(() -> new UnauthorizedException(
                        "Beneficiary not found or does not belong to you"));

        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponseDTO mapToDTO(Beneficiary b) {
        BeneficiaryResponseDTO dto = new BeneficiaryResponseDTO();
        dto.setId(b.getId());
        dto.setAccountNumber(b.getAccountNumber());
        dto.setAccountHolderName(b.getAccountHolderName());
        dto.setNickName(b.getNickName());
        dto.setBankName(b.getBankName());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}