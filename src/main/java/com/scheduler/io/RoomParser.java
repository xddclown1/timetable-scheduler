package main.java.com.scheduler.io;

import com.scheduler.domain.Room;
import com.scheduler.domain.TimeSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses rooms from CSV file.
 * Format: roomId,name,capacity,features(semicolon-separated),unavailableSlots(semicolon-separated slot IDs)
 * Example: R001,Lecture Hall A,100,projector;whiteboard,
 */
public class RoomParser extends CsvParser<Room> {
    
    private final Map<String, TimeSlot> timeSlotMap;

    public RoomParser(Map<String, TimeSlot> timeSlotMap) {
        super("rooms.csv");
        this.timeSlotMap = timeSlotMap;
    }

    @Override
    protected Room parseLine(String line, int lineNumber) throws ParseException {
        String[] fields = splitCsvLine(line);
        validateFieldCount(fields, 3, lineNumber);

        String roomId = fields[0];
        String name = fields[1];
        int capacity = parseIntField(fields[2], "capacity", lineNumber);

        Set<String> features = new HashSet<>();
        if (fields.length > 3 && !fields[3].trim().isEmpty()) {
            features.addAll(parseSemicolonList(fields[3]));
        }

        Set<TimeSlot> unavailableSlots = new HashSet<>();
        if (fields.length > 4 && !fields[4].trim().isEmpty()) {
            List<String> slotIds = parseSemicolonList(fields[4]);
            for (String slotId : slotIds) {
                TimeSlot slot = timeSlotMap.get(slotId);
                if (slot == null) {
                    throw new ParseException(
                        String.format("Unknown time slot ID: '%s'", slotId),
                        fileName,
                        lineNumber
                    );
                }
                unavailableSlots.add(slot);
            }
        }

        return Room.builder()
            .id(roomId)
            .name(name)
            .capacity(capacity)
            .features(features)
            .unavailableTimeSlots(unavailableSlots)
            .build();
    }
}
