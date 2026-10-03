import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class AllocationManager {
    private ArrayList<Allocation> allocations;
    private final String FILE_NAME = "allocations.txt";
    private StudentManager studentManager;
    private RoomManager roomManager;

    public AllocationManager(StudentManager studentManager, RoomManager roomManager) {
        this.studentManager = studentManager;
        this.roomManager = roomManager;
        allocations = new ArrayList<>();
        loadAllocations();
    }

    public boolean allocateRoom(int studentId, int roomNumber) {
        Student student = studentManager.searchStudent(studentId);
        if (student == null) {
            return false;
        }

        Room room = roomManager.searchRoom(roomNumber);
        if (room == null) {
            return false;
        }

        if (getAllocationByStudent(studentId) != null) {
            return false;
        }

        if (!room.hasVacancy()) {
            return false;
        }

        Allocation allocation = new Allocation(studentId, roomNumber);
        allocations.add(allocation);
        roomManager.addOccupant(roomNumber);
        saveAllocations();
        return true;
    }

    public Allocation getAllocationByStudent(int studentId) {
        for (Allocation allocation : allocations) {
            if (allocation.getStudentId() == studentId) {
                return allocation;
            }
        }
        return null;
    }

    public ArrayList<Allocation> getAllocationsByRoom(int roomNumber) {
        ArrayList<Allocation> result = new ArrayList<>();
        for (Allocation allocation : allocations) {
            if (allocation.getRoomNumber() == roomNumber) {
                result.add(allocation);
            }
        }
        return result;
    }

    public boolean removeAllocation(int studentId) {
        Allocation allocation = getAllocationByStudent(studentId);
        if (allocation == null) {
            return false;
        }

        allocations.remove(allocation);
        roomManager.removeOccupant(allocation.getRoomNumber());
        saveAllocations();
        return true;
    }

    public ArrayList<Allocation> getAllocations() {
        return new ArrayList<>(allocations);
    }

    public void sortByStudentId() {
        allocations.sort(Comparator.comparingInt(Allocation::getStudentId));
        saveAllocations();
    }

    private void saveAllocations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Allocation allocation : allocations) {
                writer.write(allocation.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving allocation data.");
        }
    }

    private void loadAllocations() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length != 2) {
                    continue;
                }

                try {
                    int studentId = Integer.parseInt(data[0].trim());
                    int roomNumber = Integer.parseInt(data[1].trim());

                    Student student = studentManager.searchStudent(studentId);
                    Room room = roomManager.searchRoom(roomNumber);

                    if (student == null || room == null) {
                        continue;
                    }

                    if (getAllocationByStudent(studentId) != null) {
                        continue;
                    }

                    Allocation allocation = new Allocation(studentId, roomNumber);
                    allocations.add(allocation);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid allocation record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while loading allocation data.");
        }
    }
}