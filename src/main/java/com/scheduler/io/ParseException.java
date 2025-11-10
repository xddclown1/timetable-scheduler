package com.scheduler.io;

/**
 * Exception thrown when parsing input files fails.
 */
public class ParseException extends Exception {
    private final String fileName;
    private final int lineNumber;

    public ParseException(String message, String fileName, int lineNumber) {
        super(String.format("%s (file: %s, line: %d)", message, fileName, lineNumber));
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public ParseException(String message, String fileName, int lineNumber, Throwable cause) {
        super(String.format("%s (file: %s, line: %d)", message, fileName, lineNumber), cause);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
