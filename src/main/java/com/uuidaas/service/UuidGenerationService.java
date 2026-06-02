package com.uuidaas.service;

import com.uuidaas.model.GeneratedUuid;
import com.uuidaas.repository.UuidRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidGenerationService {

    private static final Logger logger = LogManager.getLogger(UuidGenerationService.class);

    private final UuidRepository uuidRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UuidGenerationService(UuidRepository uuidRepository) {
        this.uuidRepository = uuidRepository;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<GeneratedUuid> searchByRequestedBy(String requestedBy) {
        logger.info("Searching UUIDs requested by: {}", requestedBy);
        String sql = "SELECT * FROM generated_uuids WHERE requested_by = '" + requestedBy + "'";
        return entityManager.createNativeQuery(sql, GeneratedUuid.class).getResultList();
    }

    public GeneratedUuid generateUuid(String requestedBy) {
        logger.info("UUID generation requested by: {}", requestedBy);

        String uuid = UUID.randomUUID().toString();

        while (uuidRepository.existsByUuid(uuid)) {
            logger.warn("UUID collision detected! Regenerating... (this should never happen but we're enterprise-grade)");
            uuid = UUID.randomUUID().toString();
        }

        GeneratedUuid entity = new GeneratedUuid(uuid, requestedBy);
        GeneratedUuid saved = uuidRepository.save(entity);

        logger.info("UUID successfully generated and persisted: {}", saved.getUuid());
        return saved;
    }

    public GeneratedUuid validateUuid(String uuid) {
        logger.info("Validating UUID: {}", uuid);
        return uuidRepository.findByUuid(uuid)
                .orElseThrow(() -> new UuidNotFoundException("UUID not found in our enterprise registry: " + uuid));
    }

    public long getTotalGenerated(String requestedBy) {
        return uuidRepository.countByRequestedBy(requestedBy);
    }

    public static class UuidNotFoundException extends RuntimeException {
        public UuidNotFoundException(String message) {
            super(message);
        }
    }
}
