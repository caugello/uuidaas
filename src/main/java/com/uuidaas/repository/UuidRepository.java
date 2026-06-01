package com.uuidaas.repository;

import com.uuidaas.model.GeneratedUuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UuidRepository extends JpaRepository<GeneratedUuid, Long> {

    Optional<GeneratedUuid> findByUuid(String uuid);

    boolean existsByUuid(String uuid);

    long countByRequestedBy(String requestedBy);
}
