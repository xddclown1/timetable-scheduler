package main.java.com.scheduler.constraints;

import com.scheduler.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ensures rooms are not double-booked and respects room unavailability.
 */
public class RoomAvailabilityConstraint implements Constraint {

    @Override
    public ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        List<String> violations = new ArrayList<>();

        // Check if room is unavailable at this time
        if (!room.isAvailableAt(timeSlot)) {
            violations.add(String.format(
                "Room %s (%s) is unavailable at %s",
                room.getId(), room.getName(), timeSlot
            ));
        }

        // Check if room is already occupied at this time
        if (!schedule.isRoomAvailableAt(room.getId(), timeSlot)) {
            violations.add(String.format(
                "Room %s (%s) is already occupied at %s",
                room.getId(), room.getName(), timeSlot
            ));
        }

        if (violations.isEmpty()) {
            return ValidationResult.success(getName());
        }
        return ValidationResult.failure(getName(), violations);
    }

    @Override
    public String getName() {
        return "Room Availability";
    }

    @Override
    public boolean isHardConstraint() {
        return true;
    }
}
