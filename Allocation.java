public class Allocation {
    private int studentId;
    private int roomNumber;

    public Allocation(int studentId, int roomNumber) {
        this.studentId = studentId;
        this.roomNumber = roomNumber;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    @Override
    public String toString() {
        return studentId + "," + roomNumber;
    }
}