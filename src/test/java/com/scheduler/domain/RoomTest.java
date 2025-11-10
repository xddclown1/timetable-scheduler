package com.scheduler.domain;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void shouldCreateValidRoom() {
        Room room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .addFeature("projector")
            .addFeature("whiteboard")
            .build();

        assertEquals("R001", room.getId());
        assertEquals("Lecture Hall A", room.getName());
        assertEquals(100, room.getCapacity());
        assertTrue(room.getFeatures().contains("projector"));
        assertTrue(room.getFeatures().contains("whiteboard"));
    }

    @Test
    void shouldThrowExceptionForNullId() {
        assertThrows(NullPointerException.class, () ->
            Room.builder()
                .name("Lecture Hall A")
                .capacity(100)
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () ->
            Room.builder()
                .id("R001")
                .name("Lecture Hall A")
                .capacity(0)
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () ->
            Room.builder()
                .id("R001")
                .name("Lecture Hall A")
                .capacity(-10)
                .build()
        );
    }

    @Test
    void shouldCheckIfRoomHasAllRequiredFeatures() {
        Room room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .features(Set.of("projector", "whiteboard", "speakers"))
            .build();

        assertTrue(room.hasAllFeatures(Set.of("projector", "whiteboard")));
        assertTrue(room.hasAllFeatures(Set.of("projector")));
        assertFalse(room.hasAllFeatures(Set.of("projector", "lab-equipment")));
    }

    @Test
    void shouldCheckRoomAvailability() {
        TimeSlot unavailable = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        Room room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .addUnavailableTimeSlot(unavailable)
            .build();

        TimeSlot overlapping = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 30))
            .endTime(LocalTime.of(10, 30))
            .build();

        TimeSlot available = TimeSlot.builder()
            .slotIndex(3)
            .dayOfWeek(DayOfWeek.TUESDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        assertFalse(room.isAvailableAt(overlapping));
        assertTrue(room.isAvailableAt(available));
    }

    @Test
    void shouldCheckIfRoomCanAccommodateEnrollment() {
        Room room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(50)
            .build();

        assertTrue(room.canAccommodate(30));
        assertTrue(room.canAccommodate(50));
        assertFalse(room.canAccommodate(51));
    }

    @Test
    void shouldReturnUnmodifiableFeatures() {
        Room room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .build();

        assertThrows(UnsupportedOperationException.class, () ->
            room.getFeatures().add("new-feature")
        );
    }

    @Test
    void shouldImplementEqualsBasedOnId() {
        Room room1 = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .build();

        Room room2 = Room.builder()
            .id("R001")
            .name("Different Name")
            .capacity(50)
            .build();

        assertEquals(room1, room2);
        assertEquals(room1.hashCode(), room2.hashCode());
    }
}
