package ru.itis.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.billing.entity.BillingRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<BillingRecord, Long> {

    Optional<BillingRecord> findByTenantAndModel(String tenant, String model);

    List<BillingRecord> findByTenant(String tenant);
}