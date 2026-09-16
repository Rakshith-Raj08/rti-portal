package com.example.rti_portal.service;

import com.example.rti_portal.model.*;
import com.example.rti_portal.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request createRequest(User user, Department department, String subject,
                                  String description, RequestType requestType) {
        Request request = new Request();
        request.setUser(user);
        request.setDepartment(department);
        request.setSubject(subject);
        request.setDescription(description);
        request.setRequestType(requestType);
        request.setStatus(RequestStatus.FILED);

        LocalDate filedAt = LocalDate.now();
        request.setFiledAt(filedAt);
        request.setDueDate(calculateDueDate(filedAt, requestType, department));

        return requestRepository.save(request);
    }

    private LocalDate calculateDueDate(LocalDate filedAt, RequestType requestType, Department department) {
        return switch (requestType) {
            case LIFE_LIBERTY -> filedAt.plusDays(2);
            case THIRD_PARTY -> filedAt.plusDays(35);
            case NORMAL -> filedAt.plusDays(department.getSlaDays());
        };
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public List<Request> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    public List<Request> getRequestsByUser(Long userId) {
        return requestRepository.findByUserId(userId);
    }

    public Request updateStatus(Long requestId, RequestStatus newStatus) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));
        request.setStatus(newStatus);
        return requestRepository.save(request);
    }

    public List<Request> flagOverdueRequests() {
        List<Request> overdue = requestRepository.findByStatus(RequestStatus.FILED).stream()
                .filter(r -> r.getDueDate().isBefore(LocalDate.now()))
                .toList();

        overdue.forEach(r -> r.setStatus(RequestStatus.APPEAL_ELIGIBLE));
        return requestRepository.saveAll(overdue);
    }
}
