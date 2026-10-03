import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class StudentManager {
    private ArrayList<Student> students;
    private final String FILE_NAME = "students.txt";

    public StudentManager() {
        students = new ArrayList<>();
        loadStudents();
    }

    public boolean addStudent(Student student) {
        if (searchStudent(student.getStudentId()) != null) {
            return false;
        }
        students.add(student);
        saveStudents();
        return true;
    }

    public Student searchStudent(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }

    public boolean updateStudent(Student updatedStudent) {
        Student existing = searchStudent(updatedStudent.getStudentId());
        if (existing == null) {
            return false;
        }
        existing.setName(updatedStudent.getName());
        existing.setCourse(updatedStudent.getCourse());
        existing.setRoomPreference(updatedStudent.getRoomPreference());
        saveStudents();
        return true;
    }

    public boolean deleteStudent(int studentId) {
        Student student = searchStudent(studentId);
        if (student == null) {
            return false;
        }
        students.remove(student);
        saveStudents();
        return true;
    }

    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public void sortByStudentId() {
        students.sort(Comparator.comparingInt(Student::getStudentId));
        saveStudents();
    }

    private void saveStudents() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving student data.");
        }
    }

    private void loadStudents() {
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
                    int id = Integer.parseInt(data[0].trim());
                    String name = data[1].trim();
                    String course = data[2].trim();
                    RoomType preference = RoomType.valueOf(data[3].trim().toUpperCase());
                    Student student = new Student(id, name, course, preference);

                    if (searchStudent(id) == null) {
                        students.add(student);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid student record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while loading student data.");
        }
    }
}