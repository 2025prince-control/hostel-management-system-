# Hostel Management System

A desktop-based Hostel Management System developed in **Java** using **Java Swing & AWT** with persistent file-based storage.

---

## 📌 Features

- **📊 Dashboard**: Centralized navigation and real-time overview of hostel operations.
- **👨‍🎓 Student Registration**: Register, update, and manage student details and records.
- **🛏️ Room Management**: Add, modify, and monitor room status, capacity, and room types.
- **🔑 Room Allocation**: Efficiently allocate rooms to registered students and manage check-ins.
- **🍽️ Mess Attendance**: Track daily breakfast, lunch, and dinner attendance for hostel residents.
- **📋 Menu Planning**: Plan, schedule, and view weekly mess menus.
- **📑 Reports & Search**: Comprehensive reporting with search and filter capabilities across students, allocations, and rooms.

---

## 🛠️ Technology Stack

- **Language**: Java (JDK 8+)
- **UI Framework**: Java Swing & AWT
- **Persistence**: File I/O (`.txt` data files for lightweight local storage)

---

## 📁 Project Structure

```
.
├── Allocation.java             # Room allocation data model
├── AllocationGUI.java          # Room allocation UI
├── AllocationManager.java      # Room allocation logic & file persistence
├── Attendance.java             # Attendance data model
├── AttendanceGUI.java          # Mess attendance UI
├── AttendanceManager.java      # Attendance business logic
├── HostelGUI.java              # Main Application Dashboard & Entry Point
├── Menu.java                   # Mess menu data model
├── MenuGUI.java                # Menu planning UI
├── MenuManager.java            # Mess menu business logic
├── ReportGUI.java              # Search & reporting interface
├── Room.java                   # Room data model
├── RoomGUI.java                # Room management UI
├── RoomManager.java            # Room operations & storage
├── RoomType.java               # Room type enumeration / model
├── Student.java                # Student data model
├── StudentManager.java         # Student record operations
├── StudentRegistrationGUI.java # Student registration UI
├── allocations.txt             # Allocation records
├── attendance.txt              # Attendance logs
├── menu.txt                    # Mess menu records
├── rooms.txt                   # Room database
└── students.txt                # Student database
```

---

## 🚀 Getting Started

### Prerequisites

Ensure you have **Java Development Kit (JDK 8 or higher)** installed:

```bash
java -version
javac -version
```

### Compilation

Compile all Java source files:

```bash
javac *.java
```

### Running the Application

Launch the application using `HostelGUI`:

```bash
java HostelGUI
```

---

## 👤 Author

- **Prince** - [2025prince-control](https://github.com/2025prince-control)
