package com.example.rti_portal.service;

import com.example.rti_portal.model.*;
import com.example.rti_portal.repository.AppealRepository;
import com.example.rti_portal.repository.RequestRepository;
import com.example.rti_portal.repository.StatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final AppealRepository appealRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository,
                           StatusHistoryRepository statusHistoryRepository,
                           AppealRepository appealRepository) {
        this.requestRepository = requestRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.appealRepository = appealRepository;
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

        Request saved = requestRepository.save(request);
        logStatusChange(saved, RequestStatus.FILED, "system", "Request filed");

        return saved;
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

    public Request updateStatus(Long requestId, RequestStatus newStatus, String changedBy, String note) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        request.setStatus(newStatus);
        Request saved = requestRepository.save(request);

        logStatusChange(saved, newStatus, changedBy, note);

        return saved;
    }

    // Records every status change - this is what makes resolution-time analytics possible later
    private void logStatusChange(Request request, RequestStatus status, String changedBy, String note) {
        StatusHistory entry = new StatusHistory();
        entry.setRequest(request);
        entry.setStatus(status);
        entry.setChangedAt(LocalDateTime.now());
        entry.setChangedBy(changedBy);
        entry.setNote(note);
        statusHistoryRepository.save(entry);
    }

    public List<StatusHistory> getHistoryForRequest(Long requestId) {
        return statusHistoryRepository.findByRequestIdOrderByChangedAtAsc(requestId);
    }

    // Flags overdue requests AND creates a real Appeal record for each one
    public List<Request> flagOverdueRequests() {
        List<Request> overdue = requestRepository.findByStatus(RequestStatus.FILED).stream()
                .filter(r -> r.getDueDate().isBefore(LocalDate.now()))
                .toList();

        for (Request r : overdue) {
            r.setStatus(RequestStatus.APPEAL_ELIGIBLE);
            requestRepository.save(r);
            logStatusChange(r, RequestStatus.APPEAL_ELIGIBLE, "system", "Deadline breached, auto-flagged for appeal");

            Appeal appeal = new Appeal();
            appeal.setRequest(r);
            appeal.setFiledAt(LocalDate.now());
            appeal.setStatus(AppealStatus.FILED);
            appealRepository.save(appeal);
        }

        return overdue;
    }

    public Optional<Appeal> getAppealForRequest(Long requestId) {
        return appealRepository.findByRequestId(requestId);
    }
}