package com.karpay.ach_validation.repository;
import com.karpay.ach_validation.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);
}