import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MenuGUI extends JFrame {
    private JTextField txtDate;
    private JTextField txtBreakfast;
    private JTextField txtLunch;
    private JTextField txtDinner;
    private JButton btnSave;
    private JButton btnSearch;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClear;
    private JTable table;
    private DefaultTableModel tableModel;
    private MenuManager menuManager;

    public MenuGUI() {
        menuManager = new MenuManager();

        setTitle("Hostel Management System - Mess Menu");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createGUI();
        refreshTable();
    }

    private void createGUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("MESS MENU PLANNING", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel lblDate = new JLabel("Date:");
        JLabel lblBreakfast = new JLabel("Breakfast:");
        JLabel lblLunch = new JLabel("Lunch:");
        JLabel lblDinner = new JLabel("Dinner:");

        txtDate = new JTextField(LocalDate.now().toString());
        txtBreakfast = new JTextField();
        txtLunch = new JTextField();
        txtDinner = new JTextField();

        formPanel.add(lblDate);
        formPanel.add(txtDate);
        formPanel.add(lblBreakfast);
        formPanel.add(txtBreakfast);
        formPanel.add(lblLunch);
        formPanel.add(txtLunch);
        formPanel.add(lblDinner);
        formPanel.add(txtDinner);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        btnSave = new JButton("Save / Update");
        btnSearch = new JButton("Search");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Date", "Breakfast", "Lunch", "Dinner"};
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

        btnSave.addActionListener(e -> saveMenu());
        btnSearch.addActionListener(e -> searchMenu());
        btnDelete.addActionListener(e -> deleteMenu());
        btnRefresh.addActionListener(e -> refreshTable());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });
    }

    private void saveMenu() {
        String date = txtDate.getText().trim();
        String breakfast = txtBreakfast.getText().trim();
        String lunch = txtLunch.getText().trim();
        String dinner = txtDinner.getText().trim();

        if (date.isEmpty() || breakfast.isEmpty() || lunch.isEmpty() || dinner.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        if (date.contains(",") || breakfast.contains(",") || lunch.contains(",") || dinner.contains(",")) {
            JOptionPane.showMessageDialog(this, "Comma (,) is not allowed.");
            return;
        }

        Menu menu = new Menu(date, breakfast, lunch, dinner);
        menuManager.addMenu(menu);

        JOptionPane.showMessageDialog(this, "Menu saved successfully.");
        refreshTable();
    }

    private void searchMenu() {
        String date = txtDate.getText().trim();
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter date.");
            return;
        }

        Menu menu = menuManager.searchMenu(date);
        if (menu == null) {
            JOptionPane.showMessageDialog(this, "Menu not found.");
            return;
        }

        txtBreakfast.setText(menu.getBreakfast());
        txtLunch.setText(menu.getLunch());
        txtDinner.setText(menu.getDinner());
        JOptionPane.showMessageDialog(this, "Menu found.");
    }

    private void deleteMenu() {
        String date = txtDate.getText().trim();
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter date.");
            return;
        }

        Menu menu = menuManager.searchMenu(date);
        if (menu == null) {
            JOptionPane.showMessageDialog(this, "Menu not found.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete menu for " + date + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = menuManager.deleteMenu(date);
        if (success) {
            JOptionPane.showMessageDialog(this, "Menu deleted successfully.");
            refreshTable();
            clearForm();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Menu menu : menuManager.getAllMenus()) {
            tableModel.addRow(new Object[]{
                    menu.getDate(),
                    menu.getBreakfast(),
                    menu.getLunch(),
                    menu.getDinner()
            });
        }
    }

    private void clearForm() {
        txtDate.setText(LocalDate.now().toString());
        txtBreakfast.setText("");
        txtLunch.setText("");
        txtDinner.setText("");
        table.clearSelection();
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        txtDate.setText(tableModel.getValueAt(row, 0).toString());
        txtBreakfast.setText(tableModel.getValueAt(row, 1).toString());
        txtLunch.setText(tableModel.getValueAt(row, 2).toString());
        txtDinner.setText(tableModel.getValueAt(row, 3).toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuGUI gui = new MenuGUI();
            gui.setVisible(true);
        });
    }
}