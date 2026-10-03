import java.awt.*;
import javax.swing.*;

public class HostelGUI extends JFrame {
    private JPanel mainPanel;

    public HostelGUI() {
        setTitle("Hostel Management System");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new GridLayout(9, 1, 5, 5));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel title = new JLabel("HOSTEL SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnHome = new JButton("Dashboard");
        JButton btnStudents = new JButton("Student Registration");
        JButton btnRooms = new JButton("Room Management");
        JButton btnAllocation = new JButton("Room Allocation");
        JButton btnAttendance = new JButton("Mess Attendance");
        JButton btnMenu = new JButton("Menu Planning");
        JButton btnReports = new JButton("Reports & Search");
        JButton btnExit = new JButton("Exit");

        sidebar.add(title);
        sidebar.add(btnHome);
        sidebar.add(btnStudents);
        sidebar.add(btnRooms);
        sidebar.add(btnAllocation);
        sidebar.add(btnAttendance);
        sidebar.add(btnMenu);
        sidebar.add(btnReports);
        sidebar.add(btnExit);

        mainPanel = new JPanel(new BorderLayout());
        showDashboard();

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        btnHome.addActionListener(e -> showDashboard());
        btnStudents.addActionListener(e -> openStudentGUI());
        btnRooms.addActionListener(e -> openRoomGUI());
        btnAllocation.addActionListener(e -> openAllocationGUI());
        btnAttendance.addActionListener(e -> openAttendanceGUI());
        btnMenu.addActionListener(e -> openMenuGUI());
        btnReports.addActionListener(e -> openReportGUI());
        btnExit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void showDashboard() {
        mainPanel.removeAll();

        JPanel dashboard = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("HOSTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));

        JLabel welcome = new JLabel(
                "<html><center>Welcome to Hostel Management System<br><br>"
                        + "Select a module from the left menu<br>to manage hostel activities.</center></html>",
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.PLAIN, 20));

        dashboard.add(heading, BorderLayout.NORTH);
        dashboard.add(welcome, BorderLayout.CENTER);
        mainPanel.add(dashboard, BorderLayout.CENTER);

        refreshMainPanel();
    }

    private void openStudentGUI() {
        StudentRegistrationGUI gui = new StudentRegistrationGUI();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setVisible(true);
    }

    private void openRoomGUI() {
        RoomGUI gui = new RoomGUI();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setVisible(true);
    }

    private void openAllocationGUI() {
        AllocationGUI gui = new AllocationGUI();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setVisible(true);
    }

    private void openAttendanceGUI() {
        AttendanceGUI gui = new AttendanceGUI();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setVisible(true);
    }

    private void openMenuGUI() {
        MenuGUI gui = new MenuGUI();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setVisible(true);
    }

    private void openReportGUI() {
        ReportGUI gui = new ReportGUI();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setVisible(true);
    }

    private void refreshMainPanel() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HostelGUI gui = new HostelGUI();
            gui.setVisible(true);
        });
    }
}