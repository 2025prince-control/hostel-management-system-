import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AllocationGUI extends JFrame {
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField courseField;
    private JTextField preferenceField;
    private JComboBox<String> roomBox;
    private JTable allocationTable;
    private DefaultTableModel tableModel;
    private StudentManager studentManager;
    private RoomManager roomManager;
    private AllocationManager allocationManager;
    private ArrayList<Room> availableRooms;

    public AllocationGUI() {
        studentManager = new StudentManager();
        roomManager = new RoomManager();
        allocationManager = new AllocationManager(studentManager, roomManager);
        availableRooms = new ArrayList<>();

        setTitle("Hostel Management System - Room Allocation");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        createGUI();
        refreshTable();
        setVisible(true);
    }

    private void createGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("ROOM ALLOCATION", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        studentIdField = new JTextField();
        studentNameField = new JTextField();
        courseField = new JTextField();
        preferenceField = new JTextField();

        studentNameField.setEditable(false);
        courseField.setEditable(false);
        preferenceField.setEditable(false);

        roomBox = new JComboBox<>();

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);
        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(studentNameField);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("Room Preference:"));
        formPanel.add(preferenceField);

        JPanel roomPanel = new JPanel(new BorderLayout(10, 10));
        roomPanel.add(new JLabel("Available Preferred Room:"), BorderLayout.WEST);
        roomPanel.add(roomBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        JButton findStudentButton = new JButton("Find Student");
        JButton allocateButton = new JButton("Allocate Room");
        JButton removeButton = new JButton("Remove Allocation");
        JButton searchRoomButton = new JButton("Search Room");
        JButton sortButton = new JButton("Sort by Student");
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(findStudentButton);
        buttonPanel.add(allocateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(searchRoomButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(roomPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "Student ID",
                "Student Name",
                "Course",
                "Preference",
                "Room",
                "Room Type",
                "Capacity",
                "Occupied",
                "Vacancy"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allocationTable = new JTable(tableModel);
        allocationTable.setRowHeight(28);
        allocationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(allocationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        findStudentButton.addActionListener(e -> findStudent());
        allocateButton.addActionListener(e -> allocateRoom());
        removeButton.addActionListener(e -> removeAllocation());
        searchRoomButton.addActionListener(e -> searchRoom());
        sortButton.addActionListener(e -> sortAllocations());
        refreshButton.addActionListener(e -> refreshTable());
        clearButton.addActionListener(e -> clearFields());

        studentIdField.addActionListener(e -> findStudent());
        allocationTable.getSelectionModel().addListSelectionListener(e -> loadSelectedAllocation());
    }

    private void findStudent() {
        try {
            int studentId = getStudentId();
            Student student = studentManager.searchStudent(studentId);

            if (student == null) {
                showMessage("Student not found.");
                clearStudentDetails();
                return;
            }

            Allocation existing = allocationManager.getAllocationByStudent(studentId);
            if (existing != null) {
                Room allocatedRoom = roomManager.searchRoom(existing.getRoomNumber());
                studentNameField.setText(student.getName());
                courseField.setText(student.getCourse());
                preferenceField.setText(student.getRoomPreference().toString());

                roomBox.removeAllItems();
                if (allocatedRoom != null) {
                    roomBox.addItem(allocatedRoom.getRoomNumber() + " - " + allocatedRoom.getRoomType() + " - Already Allocated");
                }
                showMessage("This student is already allocated to Room " + existing.getRoomNumber());
                return;
            }

            studentNameField.setText(student.getName());
            courseField.setText(student.getCourse());
            preferenceField.setText(student.getRoomPreference().toString());
            loadPreferredRooms(student.getRoomPreference());
        } catch (NumberFormatException e) {
            showMessage("Enter a valid Student ID.");
        }
    }

    private void loadPreferredRooms(RoomType preferredType) {
        roomBox.removeAllItems();
        availableRooms.clear();

        ArrayList<Room> rooms = roomManager.getRoomsByType(preferredType);
        for (Room room : rooms) {
            if (room.hasVacancy()) {
                availableRooms.add(room);
                String roomInfo = room.getRoomNumber() + " - " + room.getRoomType() + " - Vacancy: " + room.getVacancy();
                roomBox.addItem(roomInfo);
            }
        }

        if (availableRooms.isEmpty()) {
            roomBox.addItem("No available " + preferredType + " room");
        }
    }

    private void allocateRoom() {
        try {
            int studentId = getStudentId();
            Student student = studentManager.searchStudent(studentId);

            if (student == null) {
                showMessage("Please find a valid student first.");
                return;
            }

            if (allocationManager.getAllocationByStudent(studentId) != null) {
                showMessage("This student already has a room.");
                return;
            }

            int selectedIndex = roomBox.getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= availableRooms.size()) {
                showMessage("No available preferred room selected.");
                return;
            }

            Room selectedRoom = availableRooms.get(selectedIndex);
            if (!selectedRoom.hasVacancy()) {
                showMessage("Selected room is already full.");
                return;
            }

            boolean success = allocationManager.allocateRoom(studentId, selectedRoom.getRoomNumber());
            if (success) {
                showMessage("Room " + selectedRoom.getRoomNumber() + " allocated successfully!");
                refreshTable();
                clearFields();
            } else {
                showMessage("Room allocation failed.");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid Student ID.");
        }
    }

    private void removeAllocation() {
        try {
            int studentId = getStudentId();
            Allocation allocation = allocationManager.getAllocationByStudent(studentId);

            if (allocation == null) {
                showMessage("No allocation found for this student.");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Remove Room " + allocation.getRoomNumber() + " allocation for Student " + studentId + "?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = allocationManager.removeAllocation(studentId);
                if (success) {
                    showMessage("Room allocation removed successfully!");
                    refreshTable();
                    clearFields();
                }
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid Student ID.");
        }
    }

    private void searchRoom() {
        String input = JOptionPane.showInputDialog(this, "Enter Room Number:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int roomNumber = Integer.parseInt(input.trim());
            Room room = roomManager.searchRoom(roomNumber);

            if (room == null) {
                showMessage("Room not found.");
                return;
            }

            tableModel.setRowCount(0);
            ArrayList<Allocation> roomAllocations = allocationManager.getAllocationsByRoom(roomNumber);
            for (Allocation allocation : roomAllocations) {
                addTableRow(allocation);
            }

            if (roomAllocations.isEmpty()) {
                showMessage("Room found, but no student is allocated to it.");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid room number.");
        }
    }

    private void sortAllocations() {
        allocationManager.sortByStudentId();
        refreshTable();
        showMessage("Allocations sorted by Student ID.");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Allocation allocation : allocationManager.getAllocations()) {
            addTableRow(allocation);
        }
    }

    private void addTableRow(Allocation allocation) {
        Student student = studentManager.searchStudent(allocation.getStudentId());
        Room room = roomManager.searchRoom(allocation.getRoomNumber());

        if (student == null || room == null) {
            return;
        }

        tableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getName(),
                student.getCourse(),
                student.getRoomPreference(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getCapacity(),
                room.getOccupied(),
                room.getVacancy()
        });
    }

    private void loadSelectedAllocation() {
        int row = allocationTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        int studentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        findStudentById(studentId);
    }

    private void findStudentById(int studentId) {
        Student student = studentManager.searchStudent(studentId);
        if (student == null) {
            return;
        }

        studentIdField.setText(String.valueOf(studentId));
        studentNameField.setText(student.getName());
        courseField.setText(student.getCourse());
        preferenceField.setText(student.getRoomPreference().toString());

        Allocation allocation = allocationManager.getAllocationByStudent(studentId);
        if (allocation != null) {
            Room room = roomManager.searchRoom(allocation.getRoomNumber());
            roomBox.removeAllItems();
            if (room != null) {
                roomBox.addItem(room.getRoomNumber() + " - " + room.getRoomType() + " - Allocated");
            }
        }
    }

    private int getStudentId() {
        String text = studentIdField.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException();
        }
        int id = Integer.parseInt(text);
        if (id <= 0) {
            throw new NumberFormatException();
        }
        return id;
    }

    private void clearStudentDetails() {
        studentNameField.setText("");
        courseField.setText("");
        preferenceField.setText("");
        roomBox.removeAllItems();
        availableRooms.clear();
    }

    private void clearFields() {
        studentIdField.setText("");
        clearStudentDetails();
        allocationTable.clearSelection();
        studentIdField.requestFocus();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AllocationGUI());
    }
}