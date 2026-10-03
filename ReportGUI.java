import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReportGUI extends JFrame {
    private StudentManager studentManager;
    private RoomManager roomManager;
    private AttendanceManager attendanceManager;
    private MenuManager menuManager;

    private JTextField txtStudentId;
    private JTextField txtRoomNumber;
    private JTextField txtMenuDate;
    private JTextArea reportArea;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReportGUI() {
        studentManager = new StudentManager();
        roomManager = new RoomManager();
        attendanceManager = new AttendanceManager(studentManager);
        menuManager = new MenuManager();

        setTitle("Hostel Management System - Reports");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("HOSTEL REPORTS & SEARCH", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JPanel searchPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        txtStudentId = new JTextField();
        txtRoomNumber = new JTextField();
        txtMenuDate = new JTextField();

        JButton btnStudent = new JButton("Search Student");
        JButton btnRoom = new JButton("Search Room");
        JButton btnMenu = new JButton("Search Menu");
        JButton btnStudents = new JButton("All Students");
        JButton btnRooms = new JButton("All Rooms");
        JButton btnAttendance = new JButton("Attendance Report");

        searchPanel.add(new JLabel("Student ID:"));
        searchPanel.add(txtStudentId);
        searchPanel.add(btnStudent);
        searchPanel.add(btnStudents);

        searchPanel.add(new JLabel("Room Number:"));
        searchPanel.add(txtRoomNumber);
        searchPanel.add(btnRoom);
        searchPanel.add(btnRooms);

        searchPanel.add(new JLabel("Menu Date:"));
        searchPanel.add(txtMenuDate);
        searchPanel.add(btnMenu);
        searchPanel.add(btnAttendance);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(searchPanel, BorderLayout.CENTER);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setLineWrap(false);
        reportArea.setWrapStyleWord(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        reportArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setBorder(BorderFactory.createTitledBorder("Report Details"));
        reportScroll.setPreferredSize(new Dimension(1000, 180));

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Report Data"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, reportScroll, tableScroll);
        splitPane.setDividerLocation(190);
        splitPane.setResizeWeight(0.25);
        splitPane.setOneTouchExpandable(true);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton btnSummary = new JButton("Hostel Summary");
        JButton btnClear = new JButton("Clear");

        bottomPanel.add(btnSummary);
        bottomPanel.add(btnClear);
        add(bottomPanel, BorderLayout.SOUTH);

        btnStudent.addActionListener(e -> searchStudent());
        btnRoom.addActionListener(e -> searchRoom());
        btnMenu.addActionListener(e -> searchMenu());
        btnStudents.addActionListener(e -> showAllStudents());
        btnRooms.addActionListener(e -> showAllRooms());
        btnAttendance.addActionListener(e -> showAttendanceReport());
        btnSummary.addActionListener(e -> showHostelSummary());
        btnClear.addActionListener(e -> clearReport());
    }

    private void searchStudent() {
        try {
            int id = Integer.parseInt(txtStudentId.getText().trim());
            Student student = studentManager.searchStudent(id);

            tableModel.setRowCount(0);
            if (student == null) {
                reportArea.setText("STUDENT NOT FOUND");
                return;
            }

            reportArea.setText(
                    "STUDENT DETAILS\n"
                            + "========================================\n"
                            + "Student ID : " + student.getStudentId() + "\n"
                            + "Name       : " + student.getName() + "\n"
                            + "Course     : " + student.getCourse() + "\n"
                            + "Room Type  : " + student.getRoomPreference()
            );

            tableModel.setColumnIdentifiers(new Object[]{
                    "Student ID", "Name", "Course", "Room Preference"
            });

            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    student.getCourse(),
                    student.getRoomPreference()
            });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid Student ID.");
        }
    }

    private void searchRoom() {
        try {
            int roomNumber = Integer.parseInt(txtRoomNumber.getText().trim());
            Room room = roomManager.searchRoom(roomNumber);

            tableModel.setRowCount(0);
            if (room == null) {
                reportArea.setText("ROOM NOT FOUND");
                return;
            }

            reportArea.setText(
                    "ROOM DETAILS\n"
                            + "========================================\n"
                            + "Room Number : " + room.getRoomNumber() + "\n"
                            + "Room Type   : " + room.getRoomType() + "\n"
                            + "Capacity    : " + room.getCapacity() + "\n"
                            + "Occupied    : " + room.getOccupied() + "\n"
                            + "Vacancy     : " + room.getVacancy() + "\n"
                            + "Status      : " + (room.isFull() ? "FULL" : "AVAILABLE")
            );

            tableModel.setColumnIdentifiers(new Object[]{
                    "Room Number", "Room Type", "Capacity", "Occupied", "Vacancy", "Status"
            });

            tableModel.addRow(new Object[]{
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getCapacity(),
                    room.getOccupied(),
                    room.getVacancy(),
                    room.isFull() ? "FULL" : "AVAILABLE"
            });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid Room Number.");
        }
    }

    private void searchMenu() {
        String date = txtMenuDate.getText().trim();
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter menu date.");
            return;
        }

        Menu menu = menuManager.searchMenu(date);
        tableModel.setRowCount(0);

        if (menu == null) {
            reportArea.setText("MENU NOT FOUND FOR DATE: " + date);
            return;
        }

        reportArea.setText(
                "MESS MENU\n"
                        + "========================================\n"
                        + "Date      : " + menu.getDate() + "\n"
                        + "Breakfast : " + menu.getBreakfast() + "\n"
                        + "Lunch     : " + menu.getLunch() + "\n"
                        + "Dinner    : " + menu.getDinner()
        );

        tableModel.setColumnIdentifiers(new Object[]{
                "Date", "Breakfast", "Lunch", "Dinner"
        });

        tableModel.addRow(new Object[]{
                menu.getDate(),
                menu.getBreakfast(),
                menu.getLunch(),
                menu.getDinner()
        });
    }

    private void showAllStudents() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new Object[]{
                "Student ID", "Name", "Course", "Room Preference"
        });

        ArrayList<Student> students = studentManager.getStudents();
        reportArea.setText(
                "STUDENT REPORT\n"
                        + "========================================\n"
                        + "Total Students : " + students.size()
        );

        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    student.getCourse(),
                    student.getRoomPreference()
            });
        }
    }

    private void showAllRooms() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new Object[]{
                "Room Number", "Room Type", "Capacity", "Occupied", "Vacancy", "Status"
        });

        ArrayList<Room> rooms = roomManager.getAllRooms();
        int totalCapacity = 0;
        int totalOccupied = 0;

        for (Room room : rooms) {
            totalCapacity += room.getCapacity();
            totalOccupied += room.getOccupied();

            tableModel.addRow(new Object[]{
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getCapacity(),
                    room.getOccupied(),
                    room.getVacancy(),
                    room.isFull() ? "FULL" : "AVAILABLE"
            });
        }

        reportArea.setText(
                "ROOM REPORT\n"
                        + "========================================\n"
                        + "Total Rooms : " + rooms.size() + "\n"
                        + "Total Beds  : " + totalCapacity + "\n"
                        + "Occupied    : " + totalOccupied + "\n"
                        + "Vacant Beds : " + (totalCapacity - totalOccupied)
        );
    }

    private void showAttendanceReport() {
        LinkedList<Attendance> list = attendanceManager.getAllAttendance();
        int present = 0;
        int absent = 0;

        for (Attendance attendance : list) {
            if (attendance.getStatus().equalsIgnoreCase("Present")) {
                present++;
            } else {
                absent++;
            }
        }

        reportArea.setText(
                "ATTENDANCE REPORT\n"
                        + "========================================\n"
                        + "Total Records : " + list.size() + "\n"
                        + "Present       : " + present + "\n"
                        + "Absent        : " + absent
        );

        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new Object[]{
                "Student ID", "Student Name", "Course", "Date", "Status"
        });

        for (Attendance attendance : list) {
            Student student = studentManager.searchStudent(attendance.getStudentId());
            String course = (student != null) ? student.getCourse() : "";

            tableModel.addRow(new Object[]{
                    attendance.getStudentId(),
                    attendance.getStudentName(),
                    course,
                    attendance.getDate(),
                    attendance.getStatus()
            });
        }
    }

    private void showHostelSummary() {
        ArrayList<Student> students = studentManager.getStudents();
        ArrayList<Room> rooms = roomManager.getAllRooms();
        LinkedList<Attendance> attendance = attendanceManager.getAllAttendance();
        ArrayList<Menu> menus = menuManager.getAllMenus();

        int totalCapacity = 0;
        int occupied = 0;

        for (Room room : rooms) {
            totalCapacity += room.getCapacity();
            occupied += room.getOccupied();
        }

        int vacant = totalCapacity - occupied;
        int present = 0;
        int absent = 0;

        for (Attendance a : attendance) {
            if (a.getStatus().equalsIgnoreCase("Present")) {
                present++;
            } else {
                absent++;
            }
        }

        reportArea.setText(
                "HOSTEL MANAGEMENT SYSTEM - SUMMARY\n"
                        + "===============================================\n\n"
                        + "STUDENT INFORMATION\n"
                        + "-----------------------------------------------\n"
                        + "Students Registered : " + students.size() + "\n\n"
                        + "ROOM INFORMATION\n"
                        + "-----------------------------------------------\n"
                        + "Total Rooms         : " + rooms.size() + "\n"
                        + "Total Bed Capacity  : " + totalCapacity + "\n"
                        + "Occupied Beds       : " + occupied + "\n"
                        + "Vacant Beds         : " + vacant + "\n\n"
                        + "ATTENDANCE INFORMATION\n"
                        + "-----------------------------------------------\n"
                        + "Attendance Records  : " + attendance.size() + "\n"
                        + "Present             : " + present + "\n"
                        + "Absent              : " + absent + "\n\n"
                        + "MESS INFORMATION\n"
                        + "-----------------------------------------------\n"
                        + "Menus Planned       : " + menus.size() + "\n"
        );

        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new Object[]{
                "Information", "Value"
        });

        tableModel.addRow(new Object[]{"Students Registered", students.size()});
        tableModel.addRow(new Object[]{"Total Rooms", rooms.size()});
        tableModel.addRow(new Object[]{"Total Bed Capacity", totalCapacity});
        tableModel.addRow(new Object[]{"Occupied Beds", occupied});
        tableModel.addRow(new Object[]{"Vacant Beds", vacant});
        tableModel.addRow(new Object[]{"Attendance Records", attendance.size()});
        tableModel.addRow(new Object[]{"Present", present});
        tableModel.addRow(new Object[]{"Absent", absent});
        tableModel.addRow(new Object[]{"Menus Planned", menus.size()});
    }

    private void clearReport() {
        txtStudentId.setText("");
        txtRoomNumber.setText("");
        txtMenuDate.setText("");
        reportArea.setText("");
        tableModel.setRowCount(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReportGUI gui = new ReportGUI();
            gui.setVisible(true);
        });
    }
}