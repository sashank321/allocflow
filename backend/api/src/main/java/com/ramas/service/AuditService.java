package com.ramas.service;

import com.ramas.entity.AuditLog;
import com.ramas.enums.AuditAction;
import com.ramas.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String actorEmail, AuditAction action, String entityType, String entityId, String details, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog(actorEmail, action, entityType, entityId, details, ipAddress);
            auditLogRepository.save(auditLog);
            log.info("AUDIT: [actor={}] [action={}] [entity={}:{}]", actorEmail, action, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to persist audit log: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
