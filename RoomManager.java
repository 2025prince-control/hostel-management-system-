import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class RoomManager {
    private ArrayList<Room> rooms;
    private final String FILE_NAME = "rooms.txt";

    public RoomManager() {
        rooms = new ArrayList<>();
        loadRooms();
    }

    public boolean addRoom(Room room) {
        if (searchRoom(room.getRoomNumber()) != null) {
            return false;
        }
        rooms.add(room);
        saveRooms();
        return true;
    }

    public Room searchRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public boolean deleteRoom(int roomNumber) {
        Room room = searchRoom(roomNumber);
        if (room == null) {
            return false;
        }
        if (room.getOccupied() > 0) {
            return false;
        }
        rooms.remove(room);
        saveRooms();
        return true;
    }

    public ArrayList<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    public ArrayList<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public ArrayList<Room> getAvailableRooms() {
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.hasVacancy()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public ArrayList<Room> getRoomsByType(RoomType type) {
        ArrayList<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getRoomType() == type) {
                result.add(room);
            }
        }
        return result;
    }

    public boolean addOccupant(int roomNumber) {
        Room room = searchRoom(roomNumber);
        if (room == null) {
            return false;
        }
        if (!room.addOccupant()) {
            return false;
        }
        saveRooms();
        return true;
    }

    public boolean removeOccupant(int roomNumber) {
        Room room = searchRoom(roomNumber);
        if (room == null) {
            return false;
        }
        if (!room.removeOccupant()) {
            return false;
        }
        saveRooms();
        return true;
    }

    public void sortByRoomNumber() {
        rooms.sort(Comparator.comparingInt(Room::getRoomNumber));
        saveRooms();
    }

    public void sortByVacancy() {
        rooms.sort(Comparator.comparingInt(Room::getVacancy).reversed());
        saveRooms();
    }

    private void saveRooms() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Room room : rooms) {
                writer.write(room.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving room data.");
        }
    }

    private void loadRooms() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length != 3) {
                    continue;
                }

                try {
                    int roomNumber = Integer.parseInt(data[0].trim());
                    RoomType type = RoomType.valueOf(data[1].trim().toUpperCase());
                    int occupied = Integer.parseInt(data[2].trim());
                    Room room = new Room(roomNumber, type, occupied);

                    if (searchRoom(roomNumber) == null) {
                        rooms.add(room);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid room record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while loading room data.");
        }
    }
}