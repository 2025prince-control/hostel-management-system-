import java.io.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class AttendanceManager {
    private LinkedList<Attendance> attendanceList;
    private final String FILE_NAME = "attendance.txt";
    private StudentManager studentManager;

    public AttendanceManager(StudentManager studentManager) {
        this.studentManager = studentManager;
        attendanceList = new LinkedList<>();
        loadAttendance();
    }

    public boolean markAttendance(int studentId, LocalDate date, String status) {
        Student student = studentManager.searchStudent(studentId);
        if (student == null) {
            return false;
        }

        Attendance existing = findAttendance(studentId, date);
        if (existing != null) {
            existing.setStatus(status);
            saveAttendance();
            return true;
        }

        Attendance attendance = new Attendance(studentId, student.getName(), date, status);
        attendanceList.add(attendance);
        saveAttendance();
        return true;
    }

    public Attendance findAttendance(int studentId, LocalDate date) {
        for (Attendance attendance : attendanceList) {
            if (attendance.getStudentId() == studentId && attendance.getDate().equals(date)) {
                return attendance;
            }
        }
        return null;
    }

    public LinkedList<Attendance> getAllAttendance() {
        return new LinkedList<>(attendanceList);
    }

    public LinkedList<Attendance> getStudentAttendance(int studentId) {
        LinkedList<Attendance> result = new LinkedList<>();
        for (Attendance attendance : attendanceList) {
            if (attendance.getStudentId() == studentId) {
                result.add(attendance);
            }
        }
        return result;
    }

    public boolean deleteAttendance(int studentId, LocalDate date) {
        Attendance attendance = findAttendance(studentId, date);
        if (attendance == null) {
            return false;
        }

        attendanceList.remove(attendance);
        saveAttendance();
        return true;
    }

    private void saveAttendance() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Attendance attendance : attendanceList) {
                writer.write(attendance.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving attendance.");
        }
    }

    private void loadAttendance() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length != 4) {
                    continue;
                }

                try {
                    int studentId = Integer.parseInt(data[0].trim());
                    String studentName = data[1].trim();
                    LocalDate date = LocalDate.parse(data[2].trim());
                    String status = data[3].trim();

                    if (!status.equalsIgnoreCase("Present") && !status.equalsIgnoreCase("Absent")) {
                        continue;
                    }

                    Attendance attendance = new Attendance(studentId, studentName, date, status);
                    attendanceList.add(attendance);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid attendance record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while loading attendance.");
        }
    }
}