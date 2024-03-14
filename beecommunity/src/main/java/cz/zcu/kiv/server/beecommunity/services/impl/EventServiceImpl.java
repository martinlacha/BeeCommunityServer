package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.jpa.dto.event.EventDto;
import cz.zcu.kiv.server.beecommunity.jpa.repository.EventRepository;
import cz.zcu.kiv.server.beecommunity.services.IEventService;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Controler for event endpoints
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {
    private final EventRepository eventRepository;

    private final ObjectMapper modelMapper;


    /**
     * Create new event
     * @param eventDto dto with event info
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> createEvent(EventDto eventDto) {
        var user = UserUtils.getUserFromSecurityContext();
        var eventEntity = modelMapper.convertEventDto(eventDto);
        eventEntity.setOwner(user);
        eventRepository.saveAndFlush(eventEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Find all apiaries for user
     * @return list of apiaries
     */
    @Override
    public ResponseEntity<LinkedHashMap<String, List<EventDto>>> getEvents() {
        var user = UserUtils.getUserFromSecurityContext();
        var entitiesList = eventRepository.findByOwnerId(user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(modelMapper.convertEventList(entitiesList));
    }

    /**
     * Delete apiary by id
     * @param eventId apiary id
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> deleteEvent(Long eventId) {
        var user = UserUtils.getUserFromSecurityContext();
        var event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(event.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        eventRepository.deleteById(eventId);
        eventRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Set event to finish status when it was completed
     * @param eventId id of finished event
     * @return status code of operation result
     */
    @Override
    public ResponseEntity<Void> finishEvent(Long eventId) {
        var user = UserUtils.getUserFromSecurityContext();
        var event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (!user.getId().equals(event.get().getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        event.get().setFinished(true);
        eventRepository.saveAndFlush(event.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
