package com.faceScan.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    @FXML
    private ImageView cameraView;

    private VideoCapture camera;
    private Timer timer;

    static {
        // Załaduj bibliotekę OpenCV (tylko raz)
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @FXML
    private void onStartClicked() {
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
                    Image imageToShow = mat2Image(frame);
                    Platform.runLater(() -> cameraView.setImage(imageToShow));
                }
            }
        }, 0, 33); // ok. 30 FPS
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
}
