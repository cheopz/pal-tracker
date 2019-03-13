package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> repository = new HashMap<Long, TimeEntry>();
    long nextAvailableId = 1L;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(nextAvailableId);
        repository.put(nextAvailableId, timeEntry);
        nextAvailableId++;
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return repository.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<TimeEntry>(repository.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);

        if(find(id) != null) {
            repository.put(id, timeEntry);
            return timeEntry;
        }

        return null;
    }

    @Override
    public void delete(long id) {
        TimeEntry timeEntry = find(id);

        if (timeEntry != null)
            repository.remove(id, timeEntry);
    }
}
