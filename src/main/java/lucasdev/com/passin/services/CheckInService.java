package lucasdev.com.passin.services;

import lombok.RequiredArgsConstructor;
import lucasdev.com.passin.domain.attendee.Attendee;
import lucasdev.com.passin.domain.checkin.CheckIn;
import lucasdev.com.passin.domain.checkin.exceptions.CheckInAlreadyExistsException;
import lucasdev.com.passin.repositories.CheckinRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckinRepository checkinRepository;

    public void registerCheckIn(Attendee attendee) {
        this.verifyCheckInExists(attendee.getId());

        CheckIn newCheckIn = new CheckIn();
        newCheckIn.setAttendee(attendee);
        newCheckIn.setCreatedAt(LocalDateTime.now());

        this.checkinRepository.save(newCheckIn);
    }

    private void verifyCheckInExists(String id) {
        var isCheckedIn = this.checkinRepository.findByAttendeeId(id);

        if(isCheckedIn.isPresent()) throw new CheckInAlreadyExistsException("Attendee already check in");
    }

    public Optional<CheckIn> getCheckIn(String id) {
        return this.checkinRepository.findByAttendeeId(id);
    }
}
