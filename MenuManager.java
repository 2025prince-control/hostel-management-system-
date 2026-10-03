import java.io.*;
import java.util.ArrayList;

public class MenuManager {
    private ArrayList<Menu> menuList;
    private final String FILE_NAME = "menu.txt";

    public MenuManager() {
        menuList = new ArrayList<>();
        loadMenus();
    }

    public boolean addMenu(Menu menu) {
        Menu existing = searchMenu(menu.getDate());
        if (existing != null) {
            existing.setBreakfast(menu.getBreakfast());
            existing.setLunch(menu.getLunch());
            existing.setDinner(menu.getDinner());
            saveMenus();
            return true;
        }

        menuList.add(menu);
        saveMenus();
        return true;
    }

    public Menu searchMenu(String date) {
        for (Menu menu : menuList) {
            if (menu.getDate().equals(date)) {
                return menu;
            }
        }
        return null;
    }

    public boolean deleteMenu(String date) {
        Menu menu = searchMenu(date);
        if (menu == null) {
            return false;
        }

        menuList.remove(menu);
        saveMenus();
        return true;
    }

    public ArrayList<Menu> getAllMenus() {
        return new ArrayList<>(menuList);
    }

    private void saveMenus() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Menu menu : menuList) {
                writer.write(menu.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving menu.");
        }
    }

    private void loadMenus() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length != 4) {
                    continue;
                }

                String date = data[0].trim();
                String breakfast = data[1].trim();
                String lunch = data[2].trim();
                String dinner = data[3].trim();
                Menu menu = new Menu(date, breakfast, lunch, dinner);
                menuList.add(menu);
            }
        } catch (IOException e) {
            System.out.println("Error while loading menu.");
        }
    }
}