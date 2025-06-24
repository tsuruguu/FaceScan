package com.faceScan.controller;

import com.faceScan.dao.AttendanceDAO;
import com.faceScan.dao.GroupDAO;
import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.model.*;
import com.faceScan.iface.IFaceDetector;
import com.faceScan.iface.IFaceRecognizer;

import javafx.scene.control.Alert.AlertType;
import com.faceScan.util.AlertFactory;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    private final IFaceDetector detector = new FaceDetector();
    private final IFaceRecognizer recognizer = new FaceRecognizer();

    @FXML private Label countLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private ImageView cameraView;
    @FXML private ListView<Group> groupListView;
    @FXML private Button addGroupButton;
    @FXML private Button deleteGroupButton;

    private VideoCapture camera;
    private Timer timer;
    private User currentUser;
    private int currentGroupId;
    private final GroupDAO groupDAO = new GroupDAO();

    @FXML private TableView<StudentPresence> attendanceTable;
    @FXML private TableColumn<StudentPresence, String> colFirstName;
    @FXML private TableColumn<StudentPresence, String> colLastName;
    @FXML private TableColumn<StudentPresence, Boolean> colPresent;


    private final ObservableList<StudentPresence> studentPresenceList = FXCollections.observableArrayList();

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadGroups();
    }

    private void loadGroups() {
        if (currentUser == null) return;
        List<Group> groups = GroupDAO.getGroupsByUserId(currentUser.getId());
        groupListView.getItems().setAll(groups);
    }

    @FXML
    public void initialize() {
        stopButton.setDisable(true);
        startButton.setDisable(false);
        countLabel.setText("Detected: 0 faces.");

        colFirstName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        colPresent.setCellValueFactory(data -> data.getValue().presentProperty());
        colPresent.setCellFactory(CheckBoxTableCell.forTableColumn(colPresent));

        attendanceTable.setItems(studentPresenceList);

        attendanceTable.setEditable(true);
        colPresent.setEditable(true);

        groupListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Group selected = groupListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openGroupView(selected);
                }
            }
        });
    }

    @FXML
    void onStartClicked() {
        startButton.setDisable(true);
        stopButton.setDisable(false);

        if (camera == null) {
            camera = new VideoCapture(0);
        }
        if (!camera.isOpened()) {
            System.out.println("Failed to open camera!");
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Mat frame = new Mat();
                if (camera.read(frame)) {
                    Mat processed = detector.detectFace(frame);

                    MatOfRect faces = new MatOfRect();
                    detector.getFaceCascade().detectMultiScale(frame, faces);

                    for (Rect rect : faces.toArray()) {
                        Mat faceMat = new Mat(frame, rect);
                        Integer recognizedId = recognizer.recognizeFace(faceMat);
                        if (recognizedId != null) {
                            Platform.runLater(() -> markStudentPresent(recognizedId));
                        }
                    }

                    int count = faces.toArray().length;

                    Image fxImage = mat2Image(processed);
                    Platform.runLater(() -> {
                        cameraView.setImage(fxImage);
                        countLabel.setText("Detected: " + count + (count == 1 ? " face" : " faces"));
                    });
                }
            }
        }, 0, 33);
    }

    private Image mat2Image(Mat frame) {
        try {
            BufferedImage bufferedImage = matToBufferedImage(frame);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            System.err.println("Failed to convert image: " + e.getMessage());
            return null;
        }
    }

    private BufferedImage matToBufferedImage(Mat original) {
        int width = original.width();
        int height = original.height();
        int channels = original.channels();

        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    @FXML
    private void onStopClicked() {
        startButton.setDisable(false);
        stopButton.setDisable(true);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (camera != null && camera.isOpened()) {
            camera.release();
            System.out.println("Camera has been closed.");
        }
        Platform.runLater(() -> cameraView.setImage(null));
    }

    @FXML
    private void onAddGroupClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New group");
        dialog.setHeaderText("Enter the name od new group:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Group newGroup = new Group(name, currentUser.getId());
            groupDAO.addGroup(newGroup);
            loadGroups();
        });
    }

    @FXML
    private void onDeleteGroupClicked() {
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            groupDAO.deleteGroup(selected.getId());
            loadGroups();
        }
    }

    private void openGroupView(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group_view.fxml"));
            Parent root = loader.load();

            GroupController controller = loader.getController();
            controller.setGroup(group.getId(), group.getName());

            Stage stage = new Stage();
            stage.setTitle("Group: " + group.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentGroupId(int groupId) {
        this.currentGroupId = groupId;
        loadStudentsForGroup(groupId);
    }

    private void loadStudentsForGroup(int groupId) {
        GroupMemberDAO groupMemberDAO = new GroupMemberDAO();
        List<Student> students = groupMemberDAO.getStudentsInGroup(groupId);

        recognizer.clearTraining();
        for (Student student : students) {
            if (student.getPhotoPath() != null && !student.getPhotoPath().isBlank()) {
                recognizer.addTrainingSample(student.getId(), student.getPhotoPath());
            }
        }

        recognizer.trainModel();

        LocalDate currentDate = LocalDate.now();
        String today = currentDate.toString();

        AttendanceDAO attendanceDAO = new AttendanceDAO();

        Platform.runLater(() -> {
            studentPresenceList.clear();

            for (Student student : students) {
                StudentPresence sp = new StudentPresence(student.getId(), student.getFirstName(), student.getLastName());
                sp.setPresent(false);

                List<Attendance> attendances = attendanceDAO.getAttendanceForStudentInGroup(student.getId(), groupId);
                Attendance todayAttendance = attendances.stream()
                        .filter(a -> a.getDate().equals(today))
                        .findFirst()
                        .orElse(null);

                if (todayAttendance != null) {
                    sp.setPresent(todayAttendance.isPresent());
                    sp.setAttendanceId(todayAttendance.getId());
                }

                sp.presentProperty().addListener((obs, oldVal, newVal) -> {
                    if (sp.getAttendanceId() != null) {
                        Attendance updated = new Attendance(sp.getAttendanceId(), sp.getId(), groupId, today, newVal);
                        attendanceDAO.updateAttendance(updated);
                    } else {
                        Attendance created = new Attendance(sp.getId(), groupId, today, newVal);
                        attendanceDAO.addAttendance(created);
                    }
                });

                studentPresenceList.add(sp);
            }
            recognizer.trainModel();
        });
    }

    void markStudentPresent(int studentId) {
        for (StudentPresence sp : studentPresenceList) {
            if (sp.getId() == studentId && !sp.isPresent()) {
                sp.setPresent(true);
                break;
            }
        }
    }

    public ObservableList<StudentPresence> getStudentPresenceList() {return studentPresenceList;}



}
