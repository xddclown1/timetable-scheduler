package main.java.com.scheduler.io;

import com.scheduler.domain.Professor;
import com.scheduler.domain.TimeSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses professors from CSV file.
 * Format: professorId,name,maxLoad,unavailableSlots(semicolon-separated slot IDs)
 * Example: P001,Dr. Smith,4,1;5;9
 */
public class ProfessorParser extends CsvParser<Professor> {
    
    private final Map<String, TimeSlot> timeSlotMap;

    public ProfessorParser(Map<String, TimeSlot> timeSlotMap) {
        super("professors.csv");
        this.timeSlotMap = timeSlotMap;
    }

    @Override
    protected Professor parseLine(String line, int lineNumber) throws ParseException {
        String[] fields = splitCsvLine(line);
        validateFieldCount(fields, 3, lineNumber);

        String professorId = fields[0];
        String name = fields[1];
        int maxLoad = parseIntField(fields[2], "maxLoad", lineNumber);

        Set<TimeSlot> unavailableSlots = new HashSet<>();
        
        if (fields.length > 3 && !fields[3].trim().isEmpty()) {
            List<String> slotIds = parseSemicolonList(fields[3]);
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

        return Professor.builder()
            .id(professorId)
            .name(name)
            .maxLoad(maxLoad)
            .unavailableTimeSlots(unavailableSlots)
            .build();
    }
}
