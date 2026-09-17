package com.example.rti_portal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OverdueRequestScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueRequestScheduler.class);

    private final RequestService requestService;

    @Autowired
    public OverdueRequestScheduler(RequestService requestService) {
        this.requestService = requestService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueRequests() {
        List<?> flagged = requestService.flagOverdueRequests();
        log.info("Scheduled overdue check ran: {} request(s) flagged for appeal", flagged.size());
    }
}
