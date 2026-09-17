package com.example.rti_portal.repository;

import com.example.rti_portal.model.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByRequestIdOrderByChangedAtAsc(Long requestId);
}
