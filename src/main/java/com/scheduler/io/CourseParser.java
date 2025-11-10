package main.java.com.scheduler.io;

import com.scheduler.domain.Course;
import com.scheduler.domain.TimeSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses courses from CSV file.
 * Format: courseId,name,duration,expectedEnrollment,professorId,requiredFeatures(semicolon-separated),preferredSlots(semicolon-separated slot IDs)
 * Example: CS101,Introduction to Programming,2,50,P001,projector;computers,1;2;3
 */
public class CourseParser extends CsvParser<Course> {
    
    private final Map<String, TimeSlot> timeSlotMap;

    public CourseParser(Map<String, TimeSlot> timeSlotMap) {
        super("courses.csv");
        this.timeSlotMap = timeSlotMap;
    }

    @Override
    protected Course parseLine(String line, int lineNumber) throws ParseException {
        String[] fields = splitCsvLine(line);
        validateFieldCount(fields, 5, lineNumber);

        String courseId = fields[0];
        String name = fields[1];
        int duration = parseIntField(fields[2], "duration", lineNumber);
        int expectedEnrollment = parseIntField(fields[3], "expectedEnrollment", lineNumber);
        String professorId = fields[4];

        Set<String> requiredFeatures = new HashSet<>();
        if (fields.length > 5 && !fields[5].trim().isEmpty()) {
            requiredFeatures.addAll(parseSemicolonList(fields[5]));
        }

        Set<TimeSlot> preferredTimeWindows = new HashSet<>();
        if (fields.length > 6 && !fields[6].trim().isEmpty()) {
            List<String> slotIds = parseSemicolonList(fields[6]);
            for (String slotId : slotIds) {
                TimeSlot slot = timeSlotMap.get(slotId);
                if (slot == null) {
                    throw new ParseException(
                        String.format("Unknown time slot ID: '%s'", slotId),
                        fileName,
                        lineNumber
                    );
                }
                preferredTimeWindows.add(slot);
            }
        }

        return Course.builder()
            .id(courseId)
            .name(name)
            .duration(duration)
            .expectedEnrollment(expectedEnrollment)
            .professorId(professorId)
            .requiredFeatures(requiredFeatures)
            .preferredTimeWindows(preferredTimeWindows)
            .build();
    }
}
