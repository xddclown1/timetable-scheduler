# Course Timetable Scheduler with TDD and Constraint Satisfaction

A robust Java console application that generates conflict-free course timetables using constraint-satisfaction algorithms with backtracking. Built with Test-Driven Development (TDD) principles and clean architecture.

## Features

- **Constraint Satisfaction Scheduling**: Backtracking algorithm with forward checking and heuristics
- **Comprehensive Constraint Validation**: Professor availability, room capacity, room features, time slot conflicts
- **Multi-slot Course Support**: Handles courses requiring consecutive time slots
- **Flexible Input**: CSV-based input files for courses, professors, rooms, and time slots
- **Configurable**: Timeout limits, seed for deterministic runs, soft/hard constraint modes
- **Test-Driven Development**: Extensive unit and integration tests with high coverage
- **Clean Architecture**: SOLID principles, separation of concerns, modular design

## Project Structure

timetable-scheduler/ ├── pom.xml # Maven configuration ├── README.md # This file ├── sample-data/ # Sample input files │ ├── courses.csv │ ├── professors.csv │ ├── rooms.csv │ └── timeslots.csv └── src/ ├── main/java/com/scheduler/ │ ├── domain/ # Domain models (Course, Professor, Room, etc.) │ ├── io/ # File parsers and writers │ ├── constraints/ # Constraint validation system │ ├── scheduler/ # Scheduling algorithms │ ├── config/ # Configuration classes │ ├── util/ # Utility classes │ └── app/ # Main application and CLI └── test/java/com/scheduler/ # Comprehensive test suite


## Requirements

- **Java**: 17 or later (LTS recommended)
- **Maven**: 3.6+ or Gradle 7+
- **Memory**: 512MB minimum

## Quick Start

### 1. Clone and Build

```bash
# Clone the repository
git clone <repository-url>
cd timetable-scheduler

# Build the project
mvn clean package

# Or with tests
mvn clean test package
2. Run with Sample Data
Copy# Run with default sample data
java -jar target/scheduler.jar

# Or using Maven
mvn exec:java -Dexec.mainClass="com.scheduler.app.SchedulerApplication"
3. Run with Custom Data
Copyjava -jar target/scheduler.jar \
  --courses data/my-courses.csv \
  --professors data/my-professors.csv \
  --rooms data/my-rooms.csv \
  --timeslots data/my-timeslots.csv \
  --out output/schedule.txt \
  --timeout 30s \
  --seed 42
Input File Formats
Time Slots (timeslots.csv)
slotId,dayOfWeek,startTime,endTime
1,MONDAY,09:00,10:00
2,MONDAY,10:00,11:00
3,TUESDAY,09:00,10:00
Fields:

slotId: Unique identifier (integer)
dayOfWeek: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
startTime: HH:mm format
endTime: HH:mm format
Professors (professors.csv)
professorId,name,maxLoad,unavailableSlots
P001,Dr. Smith,6,1;7;13
P002,Dr. Jones,6,
Fields:

professorId: Unique identifier
name: Professor's full name
maxLoad: Maximum number of courses (optional, default: unlimited)
unavailableSlots: Semicolon-separated slot IDs when unavailable (optional)
Rooms (rooms.csv)
roomId,name,capacity,features,unavailableSlots
R001,Lecture Hall A,120,projector;whiteboard;speakers,
R002,Computer Lab,50,projector;computers,1;2
Fields:

roomId: Unique identifier
name: Room name
capacity: Maximum number of students (integer)
features: Semicolon-separated list of available features (optional)
unavailableSlots: Semicolon-separated slot IDs when unavailable (optional)
Courses (courses.csv)
courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots
CS101,Introduction to Programming,2,85,P001,projector;whiteboard,2;3
CS102,Data Structures,1,75,P002,projector,
Fields:

courseId: Unique identifier
name: Course name
duration: Number of consecutive time slots required (integer >= 1)
expectedEnrollment: Expected number of students (integer)
professorId: ID of assigned professor
requiredFeatures: Semicolon-separated list of required room features (optional)
preferredSlots: Semicolon-separated slot IDs for preferred times (optional, soft constraint)
Command Line Options
Usage: java -jar scheduler.jar [OPTIONS]

Options:
  --courses <file>          Path to courses CSV file
  --professors <file>       Path to professors CSV file
  --rooms <file>            Path to rooms CSV file
  --timeslots <file>        Path to time slots CSV file
  --out <file>              Path to output file
  --verbose                 Enable verbose logging
  --soft-preferences <bool> Treat soft constraints as hard (default: false)
  --timeout <time>          Timeout (e.g., 10s, 5m, 120) (default: 60s)
  --seed <number>           Random seed for deterministic runs
  --max-iterations <number> Maximum backtracking iterations (default: 10000)
  --help                    Show help message
Algorithm Overview
The scheduler uses a backtracking algorithm with constraint satisfaction:

Variable Ordering (Course Selection Heuristic)
Courses are ordered by difficulty using multiple criteria:

Expected enrollment (descending) - larger classes are harder to schedule
Duration (descending) - multi-slot courses have fewer valid placements
Number of required features (descending) - more constraints = fewer options
Value Ordering (Room/Time Selection Heuristic)
For each course, rooms and time slots are ordered to try most promising options first:

Rooms: Best fit by capacity, then by feature match
Time slots: Preferred windows first, then chronological order
Constraint Checking
Before assigning a course, the algorithm validates:

Hard Constraints (must be satisfied):

Professor availability (no double-booking, respects unavailable times)
Room availability (no double-booking, respects unavailable times)
Room capacity >= expected enrollment
Room has all required features
Multi-slot courses occupy consecutive time slots
Soft Constraints (preferred but not required):

Course scheduled in preferred time windows
Forward Checking
The algorithm checks constraints before making assignments to prune invalid branches early.

Backtracking
When no valid assignment exists for a course, the algorithm:

Marks the course as unscheduled (if configured to continue)
Backtracks to try alternative assignments for previous courses
Continues until all courses are scheduled or timeout/max iterations reached
Running Tests
Copy# Run all tests
mvn test

# Run with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Run specific test class
mvn test -Dtest=BacktrackingSchedulerTest

# Run integration tests only
mvn test -Dtest=*IntegrationTest
Test Coverage
The project includes comprehensive tests:

Unit Tests: Domain models, constraints, parsers, heuristics
Integration Tests: End-to-end scheduling scenarios
Coverage Target: >80% line coverage
Test categories:

Domain model validation and behavior
Constraint validation logic
File parsing with error handling
Scheduling algorithm correctness
Edge cases and error conditions
Design Decisions and Trade-offs
Architecture
Clean Architecture: Separation of concerns with distinct layers (domain, application, infrastructure)
SOLID Principles: Single responsibility, dependency injection, interface segregation
Immutability: Domain models are immutable where possible for thread safety and clarity
Scheduling Algorithm
Backtracking vs. Local Search: Backtracking chosen for completeness guarantees
Heuristics: Variable and value ordering significantly improve performance
Timeout Mechanism: Prevents infinite loops on impossible schedules
Constraint System
Composable Constraints: Each constraint is a separate class implementing a common interface
Hard vs. Soft: Clear distinction allows flexible configuration
Rich Validation Results: Detailed messages help diagnose scheduling failures
File I/O
CSV Format: Simple, human-readable, widely supported
Defensive Parsing: Extensive validation with line-number error reporting
Referential Integrity: Validates foreign key relationships (e.g., professorId exists)
Limitations and Future Work
Current Limitations
Performance: Large datasets (>100 courses) may hit timeout
Optimality: Finds a feasible solution, not necessarily optimal
Single Assignment: Each course scheduled once (no recurring sessions)
No Student Conflicts: Doesn't prevent student schedule conflicts
Potential Enhancements
Advanced Heuristics: Implement more sophisticated variable/value ordering
Constraint Weighting: Allow soft constraints with different priorities
Parallel Scheduling: Multi-threaded backtracking for performance
Student Schedules: Track student enrollments and prevent conflicts
Schedule Quality Metrics: Score schedules by compactness, preferences, etc.
GUI Interface: Web or desktop interface for easier interaction
Database Support: Store schedules and historical data
Incremental Scheduling: Add/remove courses from existing schedules
Room Preferences: Professors prefer certain rooms
Time Block Constraints: Prevent scheduling across lunch breaks
Troubleshooting
Common Issues
Problem: ParseException: Unknown time slot ID

Solution: Ensure all slot IDs referenced in courses, professors, and rooms exist in timeslots.csv
Problem: IllegalArgumentException: End time must be after start time

Solution: Check time slot definitions - end time must be later than start time
Problem: Timeout with no solution

Solution: Increase timeout, reduce course count, or add more rooms/time slots
Problem: Many unscheduled courses

Solution: Check constraints - may need more rooms with required features or resolve professor conflicts
Problem: FileNotFoundException

Solution: Verify file paths are correct and files exist
Contributing
Contributions are welcome! Please:

Follow existing code style and architecture
Write tests for new features
Update documentation
Ensure all tests pass before submitting
License
This project is provided for educational purposes.

Authors
Built with Test-Driven Development and Clean Architecture principles.

Acknowledgments
Constraint Satisfaction Problem (CSP) algorithms
Backtracking search techniques
SOLID design principles
Test-Driven Development methodology

## 16. Additional Test Resources

### test-courses.csv
**Location:** `src/test/resources/test-data/test-courses.csv`

```csv
courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots
CS101,Test Course 1,1,30,P001,projector,
CS102,Test Course 2,2,25,P002,projector,1;2
test-professors.csv
Location: src/test/resources/test-data/test-professors.csv

professorId,name,maxLoad,unavailableSlots
P001,Test Professor 1,4,
P002,Test Professor 2,4,
test-rooms.csv
Location: src/test/resources/test-data/test-rooms.csv

roomId,name,capacity,features,unavailableSlots
R001,Test Room 1,50,projector;whiteboard,
R002,Test Room 2,40,projector,
test-timeslots.csv
Location: src/test/resources/test-data/test-timeslots.csv

slotId,dayOfWeek,startTime,endTime
1,MONDAY,09:00,10:00
2,MONDAY,10:00,11:00
3,TUESDAY,09:00,10:00
4,TUESDAY,10:00,11:00
17. Build and Run Script
build.sh (for Unix/Mac)
Location: Root directory

Copy#!/bin/bash

echo "Building Course Timetable Scheduler..."
echo "======================================"

# Clean and compile
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

# Run tests
echo ""
echo "Running tests..."
mvn test

if [ $? -ne 0 ]; then
    echo "Tests failed!"
    exit 1
fi

# Package
echo ""
echo "Packaging application..."
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "Packaging failed!"
    exit 1
fi

echo ""
echo "Build successful!"
echo "Run with: java -jar target/scheduler.jar"
build.bat (for Windows)
Location: Root directory

@echo off
echo Building Course Timetable Scheduler...
echo ======================================

REM Clean and compile
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b 1
)

REM Run tests
echo.
echo Running tests...
call mvn test
if %ERRORLEVEL% NEQ 0 (
    echo Tests failed!
    exit /b 1
)

REM Package
echo.
echo Packaging application...
call mvn package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Packaging failed!
    exit /b 1
)

echo.
echo Build successful!
echo Run with: java -jar target\scheduler.jar