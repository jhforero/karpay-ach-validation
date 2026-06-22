package com.karpay.ach_validation.repository;

import com.karpay.ach_validation.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    Optional<BankAccount> findByCustomerIdAndBankCodeAndAccountNumber(
            UUID customerId, String bankCode, String accountNumber);
}