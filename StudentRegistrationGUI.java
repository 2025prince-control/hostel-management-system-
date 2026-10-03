import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentRegistrationGUI extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField courseField;
    private JTextField searchField;
    private JComboBox<RoomType> preferenceBox;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentManager manager;

    public StudentRegistrationGUI() {
        manager = new StudentManager();
        setTitle("Hostel Management System");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createGUI();
        refreshTable();
        setVisible(true);
    }

    private void createGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("HOSTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        idField = new JTextField();
        nameField = new JTextField();
        courseField = new JTextField();
        preferenceBox = new JComboBox<>(RoomType.values());

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("Room Preference:"));
        formPanel.add(preferenceBox);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton registerButton = new JButton("Register");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton sortButton = new JButton("Sort by ID");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(refreshButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Student ID:"));
        searchField = new JTextField(12);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Student ID", "Name", "Course", "Room Preference"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        registerButton.addActionListener(e -> registerStudent());
        searchButton.addActionListener(e -> searchStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());
        sortButton.addActionListener(e -> sortStudents());
        refreshButton.addActionListener(e -> refreshTable());
        studentTable.getSelectionModel().addListSelectionListener(e -> loadSelectedStudent());
    }

    private void registerStudent() {
        try {
            int id = getIdFromField();
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            RoomType preference = (RoomType) preferenceBox.getSelectedItem();

            validateInput(name, course);

            Student student = new Student(id, name, course, preference);
            boolean success = manager.addStudent(student);

            if (success) {
                showMessage("Student registered successfully!");
                refreshTable();
                clearFields();
            } else {
                showMessage("Student ID already exists!");
            }
        } catch (NumberFormatException e) {
            showMessage("Student ID must be a positive number.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    private void searchStudent() {
        try {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                showMessage("Enter Student ID to search.");
                return;
            }

            int id = Integer.parseInt(text);
            Student student = manager.searchStudent(id);

            if (student == null) {
                showMessage("Student not found.");
                return;
            }

            fillFields(student);
            selectStudentInTable(student);
        } catch (NumberFormatException e) {
            showMessage("Enter a valid Student ID.");
        }
    }

    private void updateStudent() {
        try {
            int id = getIdFromField();
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            RoomType preference = (RoomType) preferenceBox.getSelectedItem();

            validateInput(name, course);

            Student updatedStudent = new Student(id, name, course, preference);
            boolean success = manager.updateStudent(updatedStudent);

            if (success) {
                showMessage("Student updated successfully!");
                refreshTable();
                clearFields();
            } else {
                showMessage("Student not found.");
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid Student ID.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int id = getIdFromField();
            Student student = manager.searchStudent(id);

            if (student == null) {
                showMessage("Student not found.");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete " + student.getName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = manager.deleteStudent(id);
                if (success) {
                    showMessage("Student deleted successfully!");
                    refreshTable();
                    clearFields();
                }
            }
        } catch (NumberFormatException e) {
            showMessage("Enter a valid Student ID.");
        }
    }

    private void loadSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        Student student = manager.searchStudent(id);
        if (student != null) {
            fillFields(student);
        }
    }

    private void fillFields(Student student) {
        idField.setText(String.valueOf(student.getStudentId()));
        nameField.setText(student.getName());
        courseField.setText(student.getCourse());
        preferenceBox.setSelectedItem(student.getRoomPreference());
    }

    private void selectStudentInTable(Student student) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int id = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
            if (id == student.getStudentId()) {
                studentTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void sortStudents() {
        manager.sortByStudentId();
        refreshTable();
        showMessage("Students sorted by ID.");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student student : manager.getStudents()) {
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    student.getCourse(),
                    student.getRoomPreference()
            });
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        courseField.setText("");
        searchField.setText("");
        preferenceBox.setSelectedIndex(0);
        studentTable.clearSelection();
        idField.requestFocus();
    }

    private void validateInput(String name, String course) {
        if (name.isEmpty() || course.isEmpty()) {
            throw new IllegalArgumentException("Please fill all fields.");
        }
        if (name.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Name cannot contain numbers.");
        }
        if (name.contains(",") || course.contains(",")) {
            throw new IllegalArgumentException("Comma is not allowed.");
        }
    }

    private int getIdFromField() {
        String text = idField.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException();
        }
        int id = Integer.parseInt(text);
        if (id <= 0) {
            throw new NumberFormatException();
        }
        return id;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRegistrationGUI());
    }
}