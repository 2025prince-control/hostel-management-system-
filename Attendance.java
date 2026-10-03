import java.time.LocalDate;

public class Attendance {
    private int studentId;
    private String studentName;
    private LocalDate date;
    private String status;

    public Attendance(int studentId, String studentName, LocalDate date, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.date = date;
        this.status = status;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return studentId + "," + studentName + "," + date + "," + status;
    }
}