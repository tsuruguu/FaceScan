package com.faceScan.controller;

import com.faceScan.iface.IFaceDetector;
import com.faceScan.iface.IFaceRecognizer;
import com.faceScan.model.FaceDetector;
import com.faceScan.model.FaceRecognizer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import org.opencv.core.MatOfRect;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    private final IFaceDetector detector = new FaceDetector();
    private final IFaceRecognizer recognizer = new FaceRecognizer();

    @FXML private Label countLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private ImageView cameraView;

    private VideoCapture camera;
    private Timer timer;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    @FXML
    private void onStartClicked() {
        startButton.setVisible(false);
        stopButton.setVisible(true);

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

        startButton.setDisable(true);
        stopButton.setDisable(false);
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
        startButton.setVisible(true);
        stopButton.setVisible(false);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (camera != null && camera.isOpened()) {
            camera.release();
            System.out.println("Kamera zamknięta");
        }
        Platform.runLater(() -> cameraView.setImage(null));

        startButton.setDisable(false);  // odblokowujemy start
        stopButton.setDisable(true);    // blokujemy stop
    }

    @FXML
    public void initialize() {
        stopButton.setVisible(false);
        startButton.setVisible(true);
        countLabel.setText("Wykryto: 0 twarzy");
    }


}
