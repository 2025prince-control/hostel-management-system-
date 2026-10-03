import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AttendanceGUI extends JFrame {
    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JTextField txtCourse;
    private JTextField txtDate;
    private JComboBox<String> cmbStatus;
    private JButton btnFindStudent;
    private JButton btnMarkAttendance;
    private JButton btnSearchAttendance;
    private JButton btnStudentHistory;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClear;
    private JTable table;
    private DefaultTableModel tableModel;
    private StudentManager studentManager;
    private AttendanceManager attendanceManager;

    public AttendanceGUI() {
        studentManager = new StudentManager();
        attendanceManager = new AttendanceManager(studentManager);

        setTitle("Hostel Management System - Mess Attendance");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createGUI();
        refreshTable();
    }

    private void createGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("MESS ATTENDANCE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        txtStudentId = new JTextField();
        txtStudentName = new JTextField();
        txtStudentName.setEditable(false);
        txtStudentName.setBackground(new Color(235, 235, 235));

        txtCourse = new JTextField();
        txtCourse.setEditable(false);
        txtCourse.setBackground(new Color(235, 235, 235));

        txtDate = new JTextField(LocalDate.now().toString());
        cmbStatus = new JComboBox<>(new String[]{"Present", "Absent"});

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(txtStudentId);
        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(txtStudentName);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(txtCourse);
        formPanel.add(new JLabel("Attendance Date:"));
        formPanel.add(txtDate);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        btnFindStudent = new JButton("Find Student");
        btnMarkAttendance = new JButton("Mark Attendance");
        btnSearchAttendance = new JButton("Search Attendance");
        btnStudentHistory = new JButton("Student History");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");

        buttonPanel.add(btnFindStudent);
        buttonPanel.add(btnMarkAttendance);
        buttonPanel.add(btnSearchAttendance);
        buttonPanel.add(btnStudentHistory);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);
        buttonPanel.add(new JLabel());

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Student ID", "Student Name", "Course", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        btnFindStudent.addActionListener(e -> findStudent());
        btnMarkAttendance.addActionListener(e -> markAttendance());
        btnSearchAttendance.addActionListener(e -> searchAttendance());
        btnStudentHistory.addActionListener(e -> studentHistory());
        btnDelete.addActionListener(e -> deleteAttendance());
        btnRefresh.addActionListener(e -> refreshTable());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });
    }

    private void findStudent() {
        String idText = txtStudentId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID.");
            return;
        }

        try {
            int studentId = Integer.parseInt(idText);
            Student student = studentManager.searchStudent(studentId);

            if (student == null) {
                txtStudentName.setText("");
                txtCourse.setText("");
                JOptionPane.showMessageDialog(this, "Student not found.");
                return;
            }

            txtStudentName.setText(student.getName());
            txtCourse.setText(student.getCourse());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        }
    }

    private void markAttendance() {
        String idText = txtStudentId.getText().trim();
        String dateText = txtDate.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID.");
            return;
        }

        if (txtStudentName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please click Find Student first.");
            return;
        }

        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Attendance Date.");
            return;
        }

        try {
            int studentId = Integer.parseInt(idText);
            LocalDate date = LocalDate.parse(dateText);
            String status = cmbStatus.getSelectedItem().toString();

            boolean success = attendanceManager.markAttendance(studentId, date, status);
            if (success) {
                JOptionPane.showMessageDialog(this, "Attendance marked successfully.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.\nUse YYYY-MM-DD.");
        }
    }

    private void searchAttendance() {
        String idText = txtStudentId.getText().trim();
        String dateText = txtDate.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID.");
            return;
        }

        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Attendance Date.");
            return;
        }

        try {
            int studentId = Integer.parseInt(idText);
            LocalDate date = LocalDate.parse(dateText);
            Attendance attendance = attendanceManager.findAttendance(studentId, date);

            tableModel.setRowCount(0);
            if (attendance == null) {
                JOptionPane.showMessageDialog(this, "Attendance record not found.");
                return;
            }

            Student student = studentManager.searchStudent(studentId);
            String course = (student != null) ? student.getCourse() : "";

            tableModel.addRow(new Object[]{
                    attendance.getStudentId(),
                    attendance.getStudentName(),
                    course,
                    attendance.getDate(),
                    attendance.getStatus()
            });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.\nUse YYYY-MM-DD.");
        }
    }

    private void studentHistory() {
        String idText = txtStudentId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID.");
            return;
        }

        try {
            int studentId = Integer.parseInt(idText);
            Student student = studentManager.searchStudent(studentId);

            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student not found.");
                return;
            }

            LinkedList<Attendance> history = attendanceManager.getStudentAttendance(studentId);
            tableModel.setRowCount(0);

            for (Attendance attendance : history) {
                tableModel.addRow(new Object[]{
                        attendance.getStudentId(),
                        attendance.getStudentName(),
                        student.getCourse(),
                        attendance.getDate(),
                        attendance.getStatus()
                });
            }

            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No attendance history found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        }
    }

    private void deleteAttendance() {
        String idText = txtStudentId.getText().trim();
        String dateText = txtDate.getText().trim();

        if (idText.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID and Attendance Date.");
            return;
        }

        try {
            int studentId = Integer.parseInt(idText);
            LocalDate date = LocalDate.parse(dateText);

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this attendance record?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = attendanceManager.deleteAttendance(studentId, date);
            if (success) {
                JOptionPane.showMessageDialog(this, "Attendance deleted successfully.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Attendance record not found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.\nUse YYYY-MM-DD.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        LinkedList<Attendance> list = attendanceManager.getAllAttendance();

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

    private void clearForm() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtCourse.setText("");
        txtDate.setText(LocalDate.now().toString());
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        txtStudentId.setText(tableModel.getValueAt(row, 0).toString());
        txtStudentName.setText(tableModel.getValueAt(row, 1).toString());
        txtCourse.setText(tableModel.getValueAt(row, 2).toString());
        txtDate.setText(tableModel.getValueAt(row, 3).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 4).toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AttendanceGUI gui = new AttendanceGUI();
            gui.setVisible(true);
        });
    }
}