package com.scheduler.app;

import com.scheduler.config.SchedulerConfiguration;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses command line arguments for the scheduler application.
 */
public class CommandLineParser {

    public static SchedulerConfiguration parse(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    arguments.put(key, args[i + 1]);
                    i++;
                } else {
                    // Flag without value (e.g., --verbose)
                    arguments.put(key, "true");
                }
            }
        }

        SchedulerConfiguration.Builder builder = SchedulerConfiguration.builder();

        if (arguments.containsKey("courses")) {
            builder.coursesFile(Paths.get(arguments.get("courses")));
        }
        if (arguments.containsKey("professors")) {
            builder.professorsFile(Paths.get(arguments.get("professors")));
        }
        if (arguments.containsKey("rooms")) {
            builder.roomsFile(Paths.get(arguments.get("rooms")));
        }
        if (arguments.containsKey("timeslots")) {
            builder.timeSlotsFile(Paths.get(arguments.get("timeslots")));
        }
        if (arguments.containsKey("out") || arguments.containsKey("output")) {
            String outputPath = arguments.getOrDefault("out", arguments.get("output"));
            builder.outputFile(Paths.get(outputPath));
        }
        if (arguments.containsKey("verbose")) {
            builder.verbose(Boolean.parseBoolean(arguments.get("verbose")));
        }
        if (arguments.containsKey("soft-preferences")) {
            builder.softPreferences(Boolean.parseBoolean(arguments.get("soft-preferences")));
        }
        if (arguments.containsKey("timeout")) {
            String timeout = arguments.get("timeout");
            // Parse timeout (e.g., "10s", "5m", "120")
            long seconds = parseTimeout(timeout);
            builder.timeoutSeconds(seconds);
        }
        if (arguments.containsKey("seed")) {
            builder.seed(Long.parseLong(arguments.get("seed")));
        }
        if (arguments.containsKey("max-iterations")) {
            builder.maxIterations(Integer.parseInt(arguments.get("max-iterations")));
        }

        return builder.build();
    }

    private static long parseTimeout(String timeout) {
        timeout = timeout.trim().toLowerCase();
        
        if (timeout.endsWith("s")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1));
        } else if (timeout.endsWith("m")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1)) * 60;
        } else if (timeout.endsWith("h")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1)) * 3600;
        } else {
            return Long.parseLong(timeout);
        }
    }

    public static void printUsage() {
        System.out.println("Usage: java -jar scheduler.jar [OPTIONS]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --courses <file>          Path to courses CSV file (default: sample-data/courses.csv)");
        System.out.println("  --professors <file>       Path to professors CSV file (default: sample-data/professors.csv)");
        System.out.println("  --rooms <file>            Path to rooms CSV file (default: sample-data/rooms.csv)");
        System.out.println("  --timeslots <file>        Path to time slots CSV file (default: sample-data/timeslots.csv)");
        System.out.println("  --out <file>              Path to output file (default: schedule.txt)");
        System.out.println("  --verbose                 Enable verbose logging");
        System.out.println("  --soft-preferences <bool> Treat soft constraints as hard (default: false)");
        System.out.println("  --timeout <time>          Timeout (e.g., 10s, 5m, 120) (default: 60s)");
        System.out.println("  --seed <number>           Random seed for deterministic runs");
        System.out.println("  --max-iterations <number> Maximum backtracking iterations (default: 10000)");
        System.out.println("  --help                    Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar scheduler.jar");
        System.out.println("  java -jar scheduler.jar --courses my-courses.csv --timeout 30s --seed 42");
        System.out.println("  java -jar scheduler.jar --verbose --soft-preferences true");
    }
}
