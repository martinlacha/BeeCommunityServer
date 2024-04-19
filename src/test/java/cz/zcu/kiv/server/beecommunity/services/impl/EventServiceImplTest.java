package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.jpa.dto.event.EventDto;
import cz.zcu.kiv.server.beecommunity.jpa.entity.EventEntity;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.EventRepository;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import cz.zcu.kiv.server.beecommunity.utils.ObjectMapper;
import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ObjectMapper modelMapper;

    @Mock
    private UserUtils userUtils;

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private UserEntity user;

    private final TestData testData = new TestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        user = testData.getUser1();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void testCreateEvent_Success() {
        EventDto eventDto = testData.getEventDto();
        when(modelMapper.convertEventDto(eventDto)).thenReturn(new EventEntity());
        ResponseEntity<Void> response = eventService.createEvent(eventDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(eventRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testGetEvents_Success() {
        when(userUtils.getUserFromSecurityContext()).thenReturn(user);
        when(eventRepository.findByOwnerIdOrderById(anyLong())).thenReturn(Collections.emptyList());

        ResponseEntity<LinkedHashMap<String, List<EventDto>>> response = eventService.getEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventRepository, times(1)).findByOwnerIdOrderById(anyLong());
    }

    @Test
    void testDeleteEvent_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = eventService.deleteEvent(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventRepository, times(1)).findById(eq(1L));
        verify(eventRepository, never()).deleteById(anyLong());
        verify(eventRepository, never()).flush();
    }

    @Test
    void testDeleteEvent_BadRequest_NotOwner() {
        EventEntity eventEntity = testData.getEventDtos().get(0);
        eventEntity.setOwner(testData.getUser2());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventEntity));

        ResponseEntity<Void> response = eventService.deleteEvent(eventEntity.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(eventRepository, never()).deleteById(anyLong());
        verify(eventRepository, never()).flush();
    }

    @Test
    void testDeleteEvent_Success() {
        EventEntity eventEntity = testData.getEventDtos().get(0);
        eventEntity.setOwner(user);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventEntity));

        ResponseEntity<Void> response = eventService.deleteEvent(eventEntity.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventRepository, times(1)).deleteById(anyLong());
        verify(eventRepository, times(1)).flush();
    }

    @Test
    void testFinishEvent_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = eventService.finishEvent(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventRepository, never()).saveAndFlush(any());
    }

    @Test
    void testFinishEvent_BadRequest_NotOwner() {
        EventEntity eventEntity = testData.getEventDtos().get(1);
        eventEntity.setOwner(testData.getUser3());
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventEntity));

        ResponseEntity<Void> response = eventService.finishEvent(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(eventRepository, times(1)).findById(any());
        verify(eventRepository, never()).saveAndFlush(any());
    }

    @Test
    void testFinishEvent_Success() {
        EventEntity eventEntity = testData.getEventDtos().get(1);
        eventEntity.setOwner(user);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventEntity));

        ResponseEntity<Void> response = eventService.finishEvent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventRepository, times(1)).findById(any());
        verify(eventRepository, times(1)).saveAndFlush(any());
    }
}
