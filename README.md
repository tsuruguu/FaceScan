# FaceScan

FaceScan is a local desktop application used for automatically registering student attendance based on real-time face recognition. It uses JavaFX for the user interface and OpenCV for face detection and recognition.

## 📌 Features

- User registration and login (professors)
- Managing student groups
- Adding and editing students (first name, last name, photo)
- Live camera preview with face detection
- Recognizing students’ faces based on previously stored images
- Recording attendance in a local SQLite database
- Viewing attendance history for a selected group

## 🧱 Architecture

The project follows the **MVC + DAO** architecture pattern.


src/<br>
└── main/  
└── java/<br>
└── com/<br>
└── faceScan/<br>
├── controller/&emsp;   &emsp;     # JavaFX controllers (LoginController, AttendanceHistoryController, etc.)<br>
├── dao/&emsp; &emsp;&emsp;&emsp;               # DAO layer: UserDAO, GroupDAO, AttendanceDAO, etc.<br>
├── model/&emsp;   &emsp;&emsp;&emsp;           # Data classes: User, Student, Group, Attendance, StudentPresence<br>
├── iface/ &emsp;   &emsp;&emsp;&emsp;          # Interfaces for face detection and recognition<br>
├── util/ &emsp;   &emsp;&emsp;&emsp;&emsp;           # Utility classes: DatabaseManager, ImagesUtils, AlertFactory<br>
├── session/  &emsp;    &emsp;&emsp;      # Session management: SessionManager<br>
└── Main.java &emsp;&emsp;&emsp;         # Application entry point


#### 📁 Directory overview:

- `controller/` – JavaFX controllers (Login, Register, Dashboard, StudentDetails, AttendanceHistory)
- `dao/` – DAO layer: UserDAO, GroupDAO, AttendanceDAO, GroupMemberDAO
- `model/` – Data models: User, Group, Student, Attendance, StudentPresence
- `iface/` – Interfaces: IFaceDetector, IFaceRecognizer
- `util/` – Utilities: DatabaseManager, ImagesUtils, AlertFactory
- `session/` – Session management: SessionManager
- `Main.java` – Application start point

## 🛠️ Technologies

- **Java 17**
- **JavaFX**
- **OpenCV 4.12.0**
- **SQLite (JDBC)**
- **JUnit 5**

## ▶️ Running the App

1. Make sure Java 17 and JavaFX are installed.
2. Compile the project using an IDE (e.g., IntelliJ) or command line.
3. Run the `Main.java` class.
4. If needed, the database (`face_scan.db`) will be created automatically.

## 🧪 Testing

The project includes unit tests (JUnit 5) located in `src/test/java`. They cover DAO classes, data models, and controllers.
