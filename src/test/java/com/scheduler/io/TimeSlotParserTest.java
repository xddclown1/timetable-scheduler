package test.java.com.scheduler.io;

import com.scheduler.domain.TimeSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotParserTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldParseValidTimeSlots() throws IOException, ParseException {
        Path file = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            writer.write("1,MONDAY,09:00,10:00\n");
            writer.write("2,MONDAY,10:00,11:00\n");
            writer.write("3,TUESDAY,09:00,10:00\n");
        }

        TimeSlotParser parser = new TimeSlotParser();
        List<TimeSlot> slots = parser.parse(file);

        assertEquals(3, slots.size());
        
        TimeSlot slot1 = slots.get(0);
        assertEquals(1, slot1.getSlotIndex());
        assertEquals(DayOfWeek.MONDAY, slot1.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), slot1.getStartTime());
        assertEquals(LocalTime.of(10, 0), slot1.getEndTime());
    }

    @Test
    void shouldThrowExceptionForInvalidDayOfWeek() throws IOException {
        Path file = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            writer.write("1,INVALIDDAY,09:00,10:00\n");
        }

        TimeSlotParser parser = new TimeSlotParser();
        assertThrows(ParseException.class, () -> parser.parse(file));
    }

    @Test
    void shouldThrowExceptionForInvalidTimeFormat() throws IOException {
        Path file = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            writer.write("1,MONDAY,9:00,10:00\n");
        }

        TimeSlotParser parser = new TimeSlotParser();
        assertThrows(ParseException.class, () -> parser.parse(file));
    }

    @Test
    void shouldSkipEmptyLines() throws IOException, ParseException {
        Path file = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            writer.write("1,MONDAY,09:00,10:00\n");
            writer.write("\n");
            writer.write("2,TUESDAY,09:00,10:00\n");
        }

        TimeSlotParser parser = new TimeSlotParser();
        List<TimeSlot> slots = parser.parse(file);

        assertEquals(2, slots.size());
    }

    @Test
    void shouldBuildSlotIdMap() throws IOException, ParseException {
        Path file = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            writer.write("1,MONDAY,09:00,10:00\n");
            writer.write("2,MONDAY,10:00,11:00\n");
        }

        TimeSlotParser parser = new TimeSlotParser();
        parser.parse(file);

        assertNotNull(parser.getTimeSlotById("1"));
        assertNotNull(parser.getTimeSlotById("2"));
        assertNull(parser.getTimeSlotById("999"));
    }
}
