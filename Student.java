public class Student {
    private int studentId;
    private String name;
    private String course;
    private RoomType roomPreference;

    public Student(int studentId, String name, String course, RoomType roomPreference) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.roomPreference = roomPreference;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public RoomType getRoomPreference() {
        return roomPreference;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setRoomPreference(RoomType roomPreference) {
        this.roomPreference = roomPreference;
    }

    @Override
    public String toString() {
        return studentId + "," + name + "," + course + "," + roomPreference;
    }
}