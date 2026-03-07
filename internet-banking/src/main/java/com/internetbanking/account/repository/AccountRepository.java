package com.internetbanking.account.repository;

import com.internetbanking.account.entity.Account;
import com.internetbanking.account.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Integer userId);
    Optional<Account> findByAccountNumber(String accountNumber);
    Long countByStatus(AccountStatus status);

}