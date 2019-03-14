package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;

    private DistributionSummary timeEntrySummary;
    private Counter actionCounter;

    public TimeEntryController(TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry) {
        this.timeEntryRepository = timeEntryRepository;

        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");

    }

    @PostMapping
    public ResponseEntity create(@RequestBody TimeEntry timeEntry) {
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntry);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());
        return new ResponseEntity(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        TimeEntry timeEntry = timeEntryRepository.find(timeEntryId);


        ResponseEntity<TimeEntry> response;

        if (timeEntry == null)
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        else{
            actionCounter.increment();
            response = new ResponseEntity(timeEntry, HttpStatus.OK);
        }

        return response;
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return new ResponseEntity(timeEntryRepository.list(), HttpStatus.OK);
    }

    @PutMapping("/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry timeEntry) {
        TimeEntry updatedTimeEntry = timeEntryRepository.update(timeEntryId, timeEntry);


        ResponseEntity<TimeEntry> response;

        if (updatedTimeEntry == null)
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        else {
            actionCounter.increment();
            response = new ResponseEntity(updatedTimeEntry, HttpStatus.OK);
        }
        return response;
    }

    @DeleteMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
