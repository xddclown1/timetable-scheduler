package test.java.com.scheduler.domain;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorTest {

    @Test
    void shouldCreateValidProfessor() {
        Professor professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .build();

        assertEquals("P001", professor.getId());
        assertEquals("Dr. Smith", professor.getName());
        assertEquals(4, professor.getMaxLoad());
        assertTrue(professor.getUnavailableTimeSlots().isEmpty());
    }

    @Test
    void shouldThrowExceptionForNullId() {
        assertThrows(NullPointerException.class, () ->
            Professor.builder()
                .name("Dr. Smith")
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForNegativeMaxLoad() {
        assertThrows(IllegalArgumentException.class, () ->
            Professor.builder()
                .id("P001")
                .name("Dr. Smith")
                .maxLoad(-1)
                .build()
        );
    }

    @Test
    void shouldCheckAvailabilityCorrectly() {
        TimeSlot unavailable = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        Professor professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .addUnavailableTimeSlot(unavailable)
            .build();

        TimeSlot overlapping = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 30))
            .endTime(LocalTime.of(10, 30))
            .build();

        TimeSlot nonOverlapping = TimeSlot.builder()
            .slotIndex(3)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .build();

        assertFalse(professor.isAvailableAt(overlapping));
        assertTrue(professor.isAvailableAt(nonOverlapping));
    }

    @Test
    void shouldReturnUnmodifiableUnavailableSlots() {
        Professor professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .build();

        assertThrows(UnsupportedOperationException.class, () ->
            professor.getUnavailableTimeSlots().add(
                TimeSlot.builder()
                    .slotIndex(1)
                    .dayOfWeek(DayOfWeek.MONDAY)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(10, 0))
                    .build()
            )
        );
    }
}