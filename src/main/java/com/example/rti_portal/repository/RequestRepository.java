package com.example.rti_portal.repository;

import com.example.rti_portal.model.Request;
import com.example.rti_portal.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatus(RequestStatus status);
    List<Request> findByUserId(Long userId);
}
