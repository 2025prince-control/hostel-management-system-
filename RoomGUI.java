import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RoomGUI extends JFrame {
    private JTextField roomNumberField;
    private JComboBox<RoomType> roomTypeBox;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomManager manager;

    public RoomGUI() {
        manager = new RoomManager();
        setTitle("Hostel Management System - Rooms");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        createGUI();
        refreshTable();
        setVisible(true);
    }

    private void createGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("ROOM MANAGEMENT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        roomNumberField = new JTextField();
        roomTypeBox = new JComboBox<>(RoomType.values());

        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumberField);
        formPanel.add(new JLabel("Room Type:"));
        formPanel.add(roomTypeBox);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        JButton addButton = new JButton("Add Room");
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete");
        JButton availableButton = new JButton("Available Rooms");
        JButton addOccupantButton = new JButton("Add Occupant");
        JButton removeOccupantButton = new JButton("Remove Occupant");
        JButton sortRoomButton = new JButton("Sort by Room");
        JButton sortVacancyButton = new JButton("Sort by Vacancy");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(availableButton);
        buttonPanel.add(addOccupantButton);
        buttonPanel.add(removeOccupantButton);
        buttonPanel.add(sortRoomButton);
        buttonPanel.add(sortVacancyButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "Room Number",
                "Room Type",
                "Capacity",
                "Occupied",
                "Vacancy",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(28);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addRoom());
        searchButton.addActionListener(e -> searchRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        availableButton.addActionListener(e -> showAvailableRooms());
        addOccupantButton.addActionListener(e -> addOccupant());
        removeOccupantButton.addActionListener(e -> removeOccupant());
        sortRoomButton.addActionListener(e -> sortByRoom());
        sortVacancyButton.addActionListener(e -> sortByVacancy());
        refreshButton.addActionListener(e -> refreshTable());
        roomTable.getSelectionModel().addListSelectionListener(e -> loadSelectedRoom());
    }

    private void addRoom() {
        try {
            int roomNumber = getRoomNumber();
            RoomType type = (RoomType) roomTypeBox.getSelectedItem();
            Room room = new Room(roomNumber, type);
            boolean success = manager.addRoom(room);

            if (success) {
                showMessage("Room added successfully!");
                refreshTable();
                clearFields();
            } else {
                showMessage("Room number already exists.");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid room number.");
        }
    }

    private void searchRoom() {
        try {
            int roomNumber = getRoomNumber();
            Room room = manager.searchRoom(roomNumber);

            if (room == null) {
                showMessage("Room not found.");
                return;
            }

            roomTypeBox.setSelectedItem(room.getRoomType());
            selectRoomInTable(room);
        } catch (NumberFormatException e) {
            showMessage("Enter a valid room number.");
        }
    }

    private void deleteRoom() {
        try {
            int roomNumber = getRoomNumber();
            Room room = manager.searchRoom(roomNumber);

            if (room == null) {
                showMessage("Room not found.");
                return;
            }

            if (room.getOccupied() > 0) {
                showMessage("Cannot delete an occupied room.");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete Room " + roomNumber + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                manager.deleteRoom(roomNumber);
                refreshTable();
                clearFields();
                showMessage("Room deleted successfully!");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid room number.");
        }
    }

    private void addOccupant() {
        try {
            int roomNumber = getRoomNumber();
            boolean success = manager.addOccupant(roomNumber);

            if (success) {
                showMessage("Occupant added successfully!");
                refreshTable();
            } else {
                showMessage("Room is full or does not exist.");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid room number.");
        }
    }

    private void removeOccupant() {
        try {
            int roomNumber = getRoomNumber();
            boolean success = manager.removeOccupant(roomNumber);

            if (success) {
                showMessage("Occupant removed successfully!");
                refreshTable();
            } else {
                showMessage("Room is empty or does not exist.");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid room number.");
        }
    }

    private void showAvailableRooms() {
        tableModel.setRowCount(0);
        for (Room room : manager.getAvailableRooms()) {
            addTableRow(room);
        }
        showMessage("Showing all available rooms.");
    }

    private void sortByRoom() {
        manager.sortByRoomNumber();
        refreshTable();
        showMessage("Rooms sorted by room number.");
    }

    private void sortByVacancy() {
        manager.sortByVacancy();
        refreshTable();
        showMessage("Rooms sorted by vacancy.");
    }

    private void loadSelectedRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        int roomNumber = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        Room room = manager.searchRoom(roomNumber);
        if (room != null) {
            roomNumberField.setText(String.valueOf(room.getRoomNumber()));
            roomTypeBox.setSelectedItem(room.getRoomType());
        }
    }

    private void selectRoomInTable(Room room) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int number = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
            if (number == room.getRoomNumber()) {
                roomTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Room room : manager.getRooms()) {
            addTableRow(room);
        }
    }

    private void addTableRow(Room room) {
        String status = room.isFull() ? "FULL" : "AVAILABLE";
        tableModel.addRow(new Object[]{
                room.getRoomNumber(),
                room.getRoomType(),
                room.getCapacity(),
                room.getOccupied(),
                room.getVacancy(),
                status
        });
    }

    private int getRoomNumber() {
        String text = roomNumberField.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException();
        }
        int roomNumber = Integer.parseInt(text);
        if (roomNumber <= 0) {
            throw new NumberFormatException();
        }
        return roomNumber;
    }

    private void clearFields() {
        roomNumberField.setText("");
        roomTypeBox.setSelectedIndex(0);
        roomTable.clearSelection();
        roomNumberField.requestFocus();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoomGUI());
    }
}