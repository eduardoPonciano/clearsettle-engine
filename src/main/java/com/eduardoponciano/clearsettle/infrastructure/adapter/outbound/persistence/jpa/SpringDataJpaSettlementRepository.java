package com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataJpaSettlementRepository extends JpaRepository<SettlementJpaEntity, UUID> {
}
