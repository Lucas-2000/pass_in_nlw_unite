package lucasdev.com.passin.services;

import lombok.RequiredArgsConstructor;
import lucasdev.com.passin.domain.attendee.Attendee;
import lucasdev.com.passin.domain.attendee.exceptions.AttendeeAlreadyExistsException;
import lucasdev.com.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import lucasdev.com.passin.domain.checkin.CheckIn;
import lucasdev.com.passin.dto.attendee.AttendeeBadgeResponseDTO;
import lucasdev.com.passin.dto.attendee.AttendeeDetailsDTO;
import lucasdev.com.passin.dto.attendee.AttendeesListResponseDTO;
import lucasdev.com.passin.dto.attendee.AttendeeBadgeDTO;
import lucasdev.com.passin.repositories.AttendeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final CheckInService checkInService;

    public List<Attendee> getAllAttendeesFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId) {
        List<Attendee> attendeesList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetailsDTO> attendeeDetailsList = attendeesList.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkInService.getCheckIn(attendee.getId());
            LocalDateTime checkedInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return new AttendeeDetailsDTO(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkedInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeeDetailsList);
    }

    public Attendee registerAttendee(Attendee newAttendee) {
        this.attendeeRepository.save(newAttendee);

        return newAttendee;
    }

    public void verifyAttendeeSubscription(String email, String eventId) {
        Optional<Attendee> isAttendeeAlreadyRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);

        if(isAttendeeAlreadyRegistered.isPresent()) {
            throw new AttendeeAlreadyExistsException("Attendee is already registered");
        }
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder) {
        Attendee attendee = this.getAttendee(attendeeId);

        var uri = UriComponentsBuilder.fromPath("/attendees/{attendeeId}/check-in").buildAndExpand(attendeeId).toUri().toString();

        AttendeeBadgeDTO attendeeBadgeDTO = new AttendeeBadgeDTO(attendee.getName(), attendee.getEmail(), uri, attendee.getEvent().getId());

        return new AttendeeBadgeResponseDTO(attendeeBadgeDTO);
    }

    public void checkInAttendee(String attendeeId) {
        Attendee attendee = this.getAttendee(attendeeId);

        this.checkInService.registerCheckIn(attendee);
    }

    private Attendee getAttendee(String attendeeId) {
        return this.attendeeRepository.findById(attendeeId).orElseThrow(() -> new AttendeeNotFoundException("Attendee not found with id " + attendeeId));
    }
}
