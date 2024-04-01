package cz.zcu.kiv.server.beecommunity.controllers.api.v1;

import cz.zcu.kiv.server.beecommunity.jpa.dto.event.EventDto;
import cz.zcu.kiv.server.beecommunity.services.IEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Controller for event endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/event")
@Tag(name = "Event")
@AllArgsConstructor
public class EventController {
    private final IEventService eventService;

    /**
     * Return list of events
     * @return list of events
     */
    @GetMapping
    ResponseEntity<LinkedHashMap<String, List<EventDto>>> getEvents() {
        return eventService.getEvents();
    }

    /**
     * Create new apiary for user
     * @param eventDto dto with information and image
     * @return status code of operation result
     */
    @PostMapping
    ResponseEntity<Void> createEvent(@RequestBody @Valid EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    /**
     * Delete event by id
     * @param eventId event id
     * @return status code of operation result
     */
    @DeleteMapping
    ResponseEntity<Void> deleteApiaryById(@RequestParam Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    /**
     * Set event to completed
     * @param eventId id of event
     * @return status code of operation result
     */
    @PutMapping
    ResponseEntity<Void> finishEventById(@RequestParam Long eventId) {
        return eventService.finishEvent(eventId);
    }
}
