package com.faceScan.controller;

import com.faceScan.dao.GroupDAO;
import com.faceScan.dao.StudentDAO;
import com.faceScan.model.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.faceScan.iface.IFaceDetector;
import com.faceScan.iface.IFaceRecognizer;


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
    void onStartClicked() {
        startButton.setDisable(true);
        stopButton.setDisable(false);

        if (camera == null) {
            camera = new VideoCapture(0);
        }
        if (!camera.isOpened()) {
            System.out.println("Nie udało się otworzyć kamery");
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
                    int count = faces.toArray().length;

                    Image fxImage = mat2Image(processed);
                    Platform.runLater(() -> {
                        cameraView.setImage(fxImage);
                        countLabel.setText("Wykryto: " + count + (count == 1 ? " twarz" : " twarzy"));
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
            System.err.println("Błąd konwersji obrazu: " + e.getMessage());
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
            System.out.println("Kamera zamknięta");
        }
        Platform.runLater(() -> cameraView.setImage(null));
    }

    @FXML
    private void onAddGroupClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nowa Grupa");
        dialog.setHeaderText("Podaj nazwę nowej grupy:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Group newGroup = new Group(name, currentUser.getId());
            GroupDAO.save(newGroup);
            loadGroups();
        });
    }

    @FXML
    private void onDeleteGroupClicked() {
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            GroupDAO.delete(selected.getId());
            loadGroups();
        }
    }

    @FXML
    private void initialize() {
        stopButton.setDisable(true);
        startButton.setDisable(false);
        countLabel.setText("Wykryto: 0 twarzy");

        groupListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Group selected = groupListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openGroupView(selected);
                }
            }
        });
    }

    private void openGroupView(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group_view.fxml"));
            Parent root = loader.load();

            GroupController controller = loader.getController();
            controller.setGroup(group.getId(), group.getName());

            Stage stage = new Stage();
            stage.setTitle("Grupa: " + group.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStudentsForGroup(int groupId) {
        StudentDAO dao = new StudentDAO();
        List<Student> list;
        try {
            list = dao.getStudentsByGroupId(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        recognizer.clearTraining();             // wyczyść stare próbki
        for (Student s : list) {
            if (!s.getPhotoPath().isBlank()) {
                recognizer.addTrainingSample(s.getId(), s.getPhotoPath());
            }
        }
    }

    public void setCurrentGroupId(int groupId) {
        this.currentGroupId = groupId;
        loadStudentsForGroup(groupId);
    }

}
