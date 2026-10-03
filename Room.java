public class Room {
    private int roomNumber;
    private RoomType roomType;
    private int capacity;
    private int occupied;

    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = calculateCapacity(roomType);
        this.occupied = 0;
    }

    public Room(int roomNumber, RoomType roomType, int occupied) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = calculateCapacity(roomType);
        this.occupied = occupied;
    }

    private int calculateCapacity(RoomType roomType) {
        switch (roomType) {
            case SINGLE:
                return 1;
            case DOUBLE:
                return 2;
            case TRIPLE:
                return 3;
            default:
                return 0;
        }
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public int getVacancy() {
        return capacity - occupied;
    }

    public boolean isFull() {
        return occupied >= capacity;
    }

    public boolean hasVacancy() {
        return occupied < capacity;
    }

    public boolean addOccupant() {
        if (isFull()) {
            return false;
        }
        occupied++;
        return true;
    }

    public boolean removeOccupant() {
        if (occupied <= 0) {
            return false;
        }
        occupied--;
        return true;
    }

    public void setOccupied(int occupied) {
        if (occupied < 0 || occupied > capacity) {
            throw new IllegalArgumentException("Invalid occupancy.");
        }
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return roomNumber + "," + roomType + "," + occupied;
    }
}