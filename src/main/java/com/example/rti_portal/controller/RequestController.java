package com.example.rti_portal.controller;

import com.example.rti_portal.model.*;
import com.example.rti_portal.repository.DepartmentRepository;
import com.example.rti_portal.repository.UserRepository;
import com.example.rti_portal.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public RequestController(RequestService requestService,
                              UserRepository userRepository,
                              DepartmentRepository departmentRepository) {
        this.requestService = requestService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Request> createRequest(@jakarta.validation.Valid @RequestBody CreateRequestBody body) {
        User user = userRepository.findById(body.userId())
                .orElseThrow(() -> new RuntimeException("User not found: " + body.userId()));
        Department department = departmentRepository.findById(body.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found: " + body.departmentId()));

        Request created = requestService.createRequest(
                user, department, body.subject(), body.description(), body.requestType()
        );
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Request> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusBody body) {
        Request updated = requestService.updateStatus(id, body.status(), body.changedBy(), body.note());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/flag-overdue")
    public List<Request> flagOverdue() {
        return requestService.flagOverdueRequests();
    }

    @GetMapping("/{id}/history")
    public List<StatusHistory> getHistory(@PathVariable Long id) {
        return requestService.getHistoryForRequest(id);
    }

    @GetMapping("/{id}/appeal")
    public ResponseEntity<Appeal> getAppeal(@PathVariable Long id) {
        return requestService.getAppealForRequest(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<Request> getByStatus(@PathVariable RequestStatus status) {
        return requestService.getRequestsByStatus(status);
    }

        public record CreateRequestBody(
            @jakarta.validation.constraints.NotNull Long userId,
            @jakarta.validation.constraints.NotNull Long departmentId,
            @jakarta.validation.constraints.NotBlank String subject,
            String description,
            @jakarta.validation.constraints.NotNull RequestType requestType) {}

    public record UpdateStatusBody(RequestStatus status, String changedBy, String note) {}
}