package lucasdev.com.passin.controllers;

import lombok.RequiredArgsConstructor;
import lucasdev.com.passin.dto.attendee.AttendeesListResponseDTO;
import lucasdev.com.passin.dto.event.EventIdDTO;
import lucasdev.com.passin.dto.event.EventRequestDTO;
import lucasdev.com.passin.dto.event.EventResponseDTO;
import lucasdev.com.passin.services.AttendeeService;
import lucasdev.com.passin.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final AttendeeService attendeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String id) {
        EventResponseDTO event = this.eventService.getEventDetail(id);

        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO body) {
        EventIdDTO eventIdDTO = this.eventService.createEvent(body);

        var uri = UriComponentsBuilder.fromPath("/events/{id}").buildAndExpand(eventIdDTO.eventId()).toUri();

        return ResponseEntity.created(uri).body(eventIdDTO);
    }

    @GetMapping("/attendees/{id}")
    public ResponseEntity<AttendeesListResponseDTO> getEventAttendees(@PathVariable String id) {
        AttendeesListResponseDTO attendeesListResponseDTO = this.attendeeService.getEventsAttendee(id);

        return ResponseEntity.ok(attendeesListResponseDTO);
    }

}
