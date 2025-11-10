package main.java.com.scheduler.io;

import com.scheduler.domain.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses time slots from CSV file.
 * Format: slotId,dayOfWeek,startTime,endTime
 * Example: 1,MONDAY,09:00,10:00
 */
public class TimeSlotParser extends CsvParser<TimeSlot> {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final Map<String, TimeSlot> slotIdMap;

    public TimeSlotParser() {
        super("timeslots.csv");
        this.slotIdMap = new HashMap<>();
    }

    @Override
    protected TimeSlot parseLine(String line, int lineNumber) throws ParseException {
        String[] fields = splitCsvLine(line);
        validateFieldCount(fields, 4, lineNumber);

        String slotIdStr = fields[0];
        String dayOfWeekStr = fields[1];
        String startTimeStr = fields[2];
        String endTimeStr = fields[3];

        int slotIndex = parseIntField(slotIdStr, "slotId", lineNumber);
        DayOfWeek dayOfWeek = parseDayOfWeek(dayOfWeekStr, lineNumber);
        LocalTime startTime = parseTime(startTimeStr, "startTime", lineNumber);
        LocalTime endTime = parseTime(endTimeStr, "endTime", lineNumber);

        TimeSlot timeSlot = TimeSlot.builder()
            .slotIndex(slotIndex)
            .dayOfWeek(dayOfWeek)
            .startTime(startTime)
            .endTime(endTime)
            .build();

        slotIdMap.put(slotIdStr, timeSlot);
        return timeSlot;
    }

    private DayOfWeek parseDayOfWeek(String value, int lineNumber) throws ParseException {
        try {
            return DayOfWeek.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ParseException(
                String.format("Invalid day of week: '%s'", value),
                fileName,
                lineNumber,
                e
            );
        }
    }

    private LocalTime parseTime(String value, String fieldName, int lineNumber) 
            throws ParseException {
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(
                String.format("Invalid time format for %s: '%s' (expected HH:mm)", 
                    fieldName, value),
                fileName,
                lineNumber,
                e
            );
        }
    }

    public TimeSlot getTimeSlotById(String slotId) {
        return slotIdMap.get(slotId);
    }

    public Map<String, TimeSlot> getSlotIdMap() {
        return new HashMap<>(slotIdMap);
    }
}
