package org.example.projecttestassignment.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.projecttestassignment.services.TimeProvider;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.logging.Logger;
@Slf4j
@Service
public class CurrentTimeProvider implements TimeProvider {
    private final Clock clock;


    public CurrentTimeProvider(Clock clock) {
        this.clock = clock;
        log.info("CurrentTimeProvider initialized with clock: {}", clock);
    }

    @Override
    public LocalDate currentDate() {
        return LocalDate.now(clock);
    }
}
