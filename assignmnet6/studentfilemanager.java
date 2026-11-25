import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab Task 1: File Handling Using Byte and Character Streams
 *
 * - Character streams (FileWriter / FileReader) are used to write and read
 *   student details in a text file.
 * - Byte streams (FileInputStream / FileOutputStream) are used to copy a
 *   binary file.
 * - Every stream is closed properly, either with try-with-resources or with
 *   an explicit finally block.
 */
public class StudentFileManager {

    private static final String STUDENT_FILE = "students.txt";
    private static final String SOURCE_BINARY = "sample.bin";
    private static final String COPIED_BINARY = "sample_copy.bin";

    public static void main(String[] args) {
        // ---------- Part A: Character streams ----------
        List<Student> students = new ArrayList<>();
        students.add(new Student(101, "Aarav Sharma", "B.Tech CSE", 88.5));
        students.add(new Student(102, "Priya Patel", "B.Tech IT", 92.0));
        students.add(new Student(103, "Rohan Verma", "BCA", 76.25));
        students.add(new Student(104, "Sneha Iyer", "B.Sc CS", 81.75));

        System.out.println("=== Writing student records using FileWriter ===");
        writeStudents(students, STUDENT_FILE);

        System.out.println("\n=== Reading student records using FileReader ===");
        List<Student> loaded = readStudents(STUDENT_FILE);
        System.out.printf("%-8s %-15s %-12s %6s%n", "Roll No", "Name", "Course", "Marks");
        System.out.println("-------------------------------------------");
        for (Student s : loaded) {
            System.out.println(s);
        }
        System.out.println("Total records read: " + loaded.size());

        // ---------- Part B: Byte streams ----------
        // Use a binary file given on the command line (e.g. an image),
        // otherwise create a sample binary file to copy.
        String source = (args.length > 0) ? args[0] : SOURCE_BINARY;
        if (args.length == 0) {
            createSampleBinaryFile(SOURCE_BINARY);
        }

        System.out.println("\n=== Copying binary file using FileInputStream / FileOutputStream ===");
        long bytesCopied = copyBinaryFile(source, COPIED_BINARY);
        if (bytesCopied >= 0) {
            System.out.println("Copied " + bytesCopied + " bytes: " + source + " -> " + COPIED_BINARY);
            System.out.println("Files identical: " + filesAreIdentical(source, COPIED_BINARY));
        }
    }

    // ------------------------------------------------------------------
    // Character stream: write student details with FileWriter
    // ------------------------------------------------------------------
    static void writeStudents(List<Student> students, String fileName) {
        // try-with-resources closes the FileWriter automatically
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Student s : students) {
                writer.write(s.toFileLine());
                writer.write(System.lineSeparator());
            }
            System.out.println(students.size() + " records written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing student file: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Character stream: read student details with FileReader
    // ------------------------------------------------------------------
    static List<Student> readStudents(String fileName) {
        List<Student> students = new ArrayList<>();
        StringBuilder content = new StringBuilder();

        // Read the whole file character by character (in chunks) using FileReader
        try (FileReader reader = new FileReader(fileName)) {
            char[] buffer = new char[256];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Student file not found: " + fileName);
            return students;
        } catch (IOException e) {
            System.err.println("Error reading student file: " + e.getMessage());
            return students;
        }

        // Parse each line back into a Student object
        String[] lines = content.toString().split("\\R");
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                students.add(Student.fromFileLine(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping malformed record: " + line);
            }
        }
        return students;
    }

    // ------------------------------------------------------------------
    // Byte stream: copy a binary file with FileInputStream / FileOutputStream
    // (closed explicitly in a finally block to show the classic approach)
    // ------------------------------------------------------------------
    static long copyBinaryFile(String sourceFile, String destFile) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalBytes = 0;

        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            return totalBytes;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            return -1;
        } catch (IOException e) {
            System.err.println("Error while copying: " + e.getMessage());
            return -1;
        } finally {
            // Close both streams, each in its own try so one failure
            // does not prevent the other stream from being closed.
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                System.err.println("Error closing input stream: " + e.getMessage());
            }
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                System.err.println("Error closing output stream: " + e.getMessage());
            }
        }
    }

    // Creates a small binary file containing every byte value 0..255 (x4)
    static void createSampleBinaryFile(String fileName) {
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            for (int round = 0; round < 4; round++) {
                for (int b = 0; b < 256; b++) {
                    out.write(b);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not create sample binary file: " + e.getMessage());
        }
    }

    // Compares two files byte by byte using byte streams
    static boolean filesAreIdentical(String file1, String file2) {
        try (FileInputStream a = new FileInputStream(file1);
             FileInputStream b = new FileInputStream(file2)) {
            int x, y;
            do {
                x = a.read();
                y = b.read();
                if (x != y) return false;
            } while (x != -1);
            return true;
        } catch (IOException e) {
            System.err.println("Error comparing files: " + e.getMessage());
            return false;
        }
    }
}

/**
 * Simple model class for a student record.
 * Stored in the text file as: rollNo,name,course,marks
 */
class Student {
    private final int rollNo;
    private final String name;
    private final String course;
    private final double marks;

    Student(int rollNo, String name, String course, double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.course = course;
        this.marks = marks;
    }

    String toFileLine() {
        return rollNo + "," + name + "," + course + "," + marks;
    }

    static Student fromFileLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 fields");
        }
        try {
            return new Student(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    Double.parseDouble(parts[3].trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in record", e);
        }
    }

    @Override
    public String toString() {
        return String.format("%-8d %-15s %-12s %6.2f", rollNo, name, course, marks);
    }
}
