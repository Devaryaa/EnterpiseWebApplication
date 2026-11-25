import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lab Task 2: File-Based Log Analyzer
 *
 * - Reads a log file line by line using BufferedReader.
 * - Counts the total number of log entries.
 * - Identifies lines containing keywords such as ERROR and WARNING.
 * - Writes a summary and the filtered entries to a separate file
 *   using BufferedWriter.
 *
 * Usage:
 *   java LogAnalyzer                      (creates and analyzes a sample app.log)
 *   java LogAnalyzer myapp.log            (analyzes your own log file)
 *   java LogAnalyzer myapp.log out.txt    (custom output file)
 */
public class LogAnalyzer {

    private static final String DEFAULT_LOG_FILE = "app.log";
    private static final String DEFAULT_OUTPUT_FILE = "filtered_log.txt";
    private static final String[] KEYWORDS = {"ERROR", "WARNING"};

    public static void main(String[] args) {
        String logFile = (args.length > 0) ? args[0] : DEFAULT_LOG_FILE;
        String outputFile = (args.length > 1) ? args[1] : DEFAULT_OUTPUT_FILE;

        if (args.length == 0 && !new File(DEFAULT_LOG_FILE).exists()) {
            createSampleLog(DEFAULT_LOG_FILE);
            System.out.println("Sample log file created: " + DEFAULT_LOG_FILE);
        }

        // keyword -> number of matching lines (LinkedHashMap keeps keyword order)
        Map<String, Integer> keywordCounts = new LinkedHashMap<>();
        for (String k : KEYWORDS) {
            keywordCounts.put(k, 0);
        }
        List<String> filteredEntries = new ArrayList<>();
        int totalEntries = 0;

        // ---------- Read and analyze with BufferedReader ----------
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;               // blank lines are not log entries
                }
                totalEntries++;

                String upper = line.toUpperCase();
                boolean matched = false;
                for (String keyword : KEYWORDS) {
                    if (upper.contains(keyword)) {
                        keywordCounts.put(keyword, keywordCounts.get(keyword) + 1);
                        matched = true;
                    }
                }
                if (matched) {
                    filteredEntries.add("[Line " + lineNumber + "] " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Log file not found: " + logFile);
            return;
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            return;
        }

        // ---------- Write results with BufferedWriter ----------
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            writer.write("========== LOG ANALYSIS REPORT ==========");
            writer.newLine();
            writer.write("Source file    : " + logFile);
            writer.newLine();
            writer.write("Generated at   : " + timestamp);
            writer.newLine();
            writer.write("Total entries  : " + totalEntries);
            writer.newLine();
            for (Map.Entry<String, Integer> entry : keywordCounts.entrySet()) {
                writer.write(String.format("%-15s: %d", entry.getKey() + " lines", entry.getValue()));
                writer.newLine();
            }
            writer.write("Filtered lines : " + filteredEntries.size());
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.newLine();
            writer.write("---------- FILTERED ENTRIES ----------");
            writer.newLine();
            for (String entry : filteredEntries) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
            return;
        }

        // ---------- Console summary ----------
        System.out.println("\n===== Log Analysis Summary =====");
        System.out.println("Log file analyzed : " + logFile);
        System.out.println("Total log entries : " + totalEntries);
        for (Map.Entry<String, Integer> entry : keywordCounts.entrySet()) {
            System.out.printf("%-18s: %d%n", entry.getKey() + " lines", entry.getValue());
        }
        System.out.println("Filtered entries saved to: " + outputFile);
    }

    // Creates a sample application log so the program can be demonstrated
    static void createSampleLog(String fileName) {
        String[] sampleLines = {
            "2026-11-10 09:00:01 INFO    Application started successfully",
            "2026-11-10 09:00:05 INFO    Connected to database 'studentdb'",
            "2026-11-10 09:01:12 DEBUG   Loading configuration from config.properties",
            "2026-11-10 09:02:30 WARNING Disk usage at 85% on /var/data",
            "2026-11-10 09:03:45 INFO    User 'admin' logged in",
            "2026-11-10 09:05:10 ERROR   NullPointerException in ReportService.generate()",
            "2026-11-10 09:06:00 INFO    Scheduled backup job started",
            "2026-11-10 09:07:22 WARNING Response time 2300ms exceeds threshold of 2000ms",
            "2026-11-10 09:08:15 ERROR   Failed to send email notification: SMTP timeout",
            "2026-11-10 09:09:40 INFO    Backup job completed",
            "",
            "2026-11-10 09:10:05 DEBUG   Cache hit ratio: 0.93",
            "2026-11-10 09:11:30 ERROR   Database connection lost, retrying (attempt 1/3)",
            "2026-11-10 09:11:35 INFO    Database connection re-established",
            "2026-11-10 09:12:50 WARNING Deprecated API /v1/students called by client 10.0.0.42",
            "2026-11-10 09:15:00 INFO    User 'admin' logged out"
        };

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : sampleLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not create sample log: " + e.getMessage());
        }
    }
}
