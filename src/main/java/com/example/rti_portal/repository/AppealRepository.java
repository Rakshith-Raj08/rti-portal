package com.example.rti_portal.repository;

import com.example.rti_portal.model.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppealRepository extends JpaRepository<Appeal, Long> {
    Optional<Appeal> findByRequestId(Long requestId);
}
