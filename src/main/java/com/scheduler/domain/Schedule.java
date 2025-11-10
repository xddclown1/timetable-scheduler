package main.java.com.scheduler.domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a complete schedule with all course assignments.
 * Provides query methods for conflict detection and validation.
 */
public class Schedule {
    private final List<CourseAssignment> assignments;
    private final Map<String, CourseAssignment> courseIdToAssignment;
    private final Map<String, List<CourseAssignment>> professorIdToAssignments;
    private final Map<String, List<CourseAssignment>> roomIdToAssignments;

    public Schedule() {
        this.assignments = new ArrayList<>();
        this.courseIdToAssignment = new HashMap<>();
        this.professorIdToAssignments = new HashMap<>();
        this.roomIdToAssignments = new HashMap<>();
    }

    public void addAssignment(CourseAssignment assignment) {
        Objects.requireNonNull(assignment, "Assignment cannot be null");
        
        String courseId = assignment.getCourse().getId();
        if (courseIdToAssignment.containsKey(courseId)) {
            throw new IllegalStateException(
                "Course " + courseId + " is already scheduled");
        }
        
        assignments.add(assignment);
        courseIdToAssignment.put(courseId, assignment);
        
        String professorId = assignment.getCourse().getProfessorId();
        professorIdToAssignments
            .computeIfAbsent(professorId, k -> new ArrayList<>())
            .add(assignment);
        
        String roomId = assignment.getRoom().getId();
        roomIdToAssignments
            .computeIfAbsent(roomId, k -> new ArrayList<>())
            .add(assignment);
    }

    public void removeAssignment(CourseAssignment assignment) {
        String courseId = assignment.getCourse().getId();
        assignments.remove(assignment);
        courseIdToAssignment.remove(courseId);
        
        String professorId = assignment.getCourse().getProfessorId();
        List<CourseAssignment> profAssignments = professorIdToAssignments.get(professorId);
        if (profAssignments != null) {
            profAssignments.remove(assignment);
        }
        
        String roomId = assignment.getRoom().getId();
        List<CourseAssignment> roomAssignments = roomIdToAssignments.get(roomId);
        if (roomAssignments != null) {
            roomAssignments.remove(assignment);
        }
    }

    public List<CourseAssignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }

    public Optional<CourseAssignment> getAssignment(String courseId) {
        return Optional.ofNullable(courseIdToAssignment.get(courseId));
    }

    public List<CourseAssignment> getAssignmentsForProfessor(String professorId) {
        return professorIdToAssignments.getOrDefault(professorId, Collections.emptyList());
    }

    public List<CourseAssignment> getAssignmentsForRoom(String roomId) {
        return roomIdToAssignments.getOrDefault(roomId, Collections.emptyList());
    }

    public boolean isProfessorAvailableAt(String professorId, TimeSlot timeSlot) {
        List<CourseAssignment> profAssignments = getAssignmentsForProfessor(professorId);
        return profAssignments.stream()
            .flatMap(a -> a.getTimeSlots().stream())
            .noneMatch(slot -> slot.overlapsWith(timeSlot));
    }

    public boolean isRoomAvailableAt(String roomId, TimeSlot timeSlot) {
        List<CourseAssignment> roomAssignments = getAssignmentsForRoom(roomId);
        return roomAssignments.stream()
            .flatMap(a -> a.getTimeSlots().stream())
            .noneMatch(slot -> slot.overlapsWith(timeSlot));
    }

    public int getScheduledCoursesCount() {
        return assignments.size();
    }

    public boolean isEmpty() {
        return assignments.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Schedule{assignments=%d}", assignments.size());
    }
}