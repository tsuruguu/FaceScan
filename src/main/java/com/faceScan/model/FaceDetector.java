package com.faceScan.model;

import com.faceScan.iface.IFaceDetector;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FaceDetector implements IFaceDetector {
    private static final String CASCADE_FILE = "/haarcascades/haarcascade_frontalface_alt.xml";
    private static CascadeClassifier faceCascade;

    static {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try (InputStream is = FaceDetector.class.getResourceAsStream(CASCADE_FILE)) {
            if (is == null) {
                throw new RuntimeException("Kaskada Haar nie znaleziona: " + CASCADE_FILE);
            }
            File tempFile = Files.createTempFile("haarcascade_frontalface_alt", ".xml").toFile();
            tempFile.deleteOnExit();
            try (FileOutputStream os = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            faceCascade = new CascadeClassifier(tempFile.getAbsolutePath());
            if (faceCascade.empty()) {
                throw new RuntimeException("Nie udało się załadować klasyfikatora z: " + tempFile.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd wczytywania kaskady Haar: " + e.getMessage(), e);
        }
    }

    @Override
    public Mat detectFace(Mat frame) {
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frame, faces);
        Rect[] faceArray = faces.toArray();

        if (faceArray.length == 0) {
            return frame;
        }

        Mat output = frame.clone();
        for (Rect rect : faceArray) {
            Imgproc.rectangle(output, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
        }
        return output;
    }

    public CascadeClassifier getFaceCascade() {
        return faceCascade;
    }
}
