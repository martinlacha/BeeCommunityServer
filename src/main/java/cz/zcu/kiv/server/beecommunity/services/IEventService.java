package cz.zcu.kiv.server.beecommunity.services;

import cz.zcu.kiv.server.beecommunity.jpa.dto.event.EventDto;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;

public interface IEventService {
    ResponseEntity<Void> createEvent(EventDto postDto);

    ResponseEntity<LinkedHashMap<String, List<EventDto>>> getEvents();

    ResponseEntity<Void> deleteEvent(Long eventId);

    ResponseEntity<Void> finishEvent(Long eventId);
}
