package com.faceScan.model;

import com.faceScan.iface.IFaceRecognizer;
import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FaceRecognizer implements IFaceRecognizer {
    private static final Size FACE_SIZE = new Size(200, 200);
    private static final String CASCADE_PATH = "/haarcascades/haarcascade_frontalface_alt.xml";

    private LBPHFaceRecognizer recognizer;
    private final List<Mat> trainingImages = new ArrayList<>();
    private final List<Integer> trainingLabels = new ArrayList<>();
    private final Map<Integer, String> labelNameMap = new HashMap<>();
    private final CascadeClassifier faceDetector;

    private static CascadeClassifier loadCascadeFromResources(String resourcePath) {
        try (InputStream is = FaceRecognizer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Kaskada Haar nie znaleziona: " + resourcePath);
            }
            File tempFile = File.createTempFile("haarcascade_frontalface_alt", ".xml");
            tempFile.deleteOnExit();
            try (FileOutputStream os = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            CascadeClassifier classifier = new CascadeClassifier(tempFile.getAbsolutePath());
            if (classifier.empty()) {
                throw new RuntimeException("Nie udało się załadować klasyfikatora z: " + tempFile.getAbsolutePath());
            }
            return classifier;
        } catch (IOException e) {
            throw new RuntimeException("Błąd wczytywania kaskady Haar: " + e.getMessage(), e);
        }
    }


    public FaceRecognizer() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.recognizer = LBPHFaceRecognizer.create();
        this.faceDetector = loadCascadeFromResources(CASCADE_PATH);
    }


    @Override
    public void clearTraining() {
        trainingImages.clear();
        trainingLabels.clear();
        labelNameMap.clear();
        recognizer = LBPHFaceRecognizer.create();
    }

    @Override
    public void addTrainingSample(int label, String imagePath) {
        Mat img = Imgcodecs.imread(imagePath);
        if (img.empty()) return;

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(img, faces);
        if (!faces.empty()) {
            Rect faceRect = faces.toArray()[0];
            Mat faceROI = new Mat(img, faceRect);
            Mat grayFace = new Mat();
            Imgproc.cvtColor(faceROI, grayFace, Imgproc.COLOR_BGR2GRAY);
            Mat resized = new Mat();
            Imgproc.resize(grayFace, resized, FACE_SIZE);

            trainingImages.add(resized);
            trainingLabels.add(label);
            labelNameMap.putIfAbsent(label, imagePath);
        }
    }

    @Override
    public void trainModel() {
        if (trainingImages.isEmpty()) return;
        Mat labelsMat = new Mat(trainingLabels.size(), 1, CvType.CV_32SC1);
        for (int i = 0; i < trainingLabels.size(); i++) {
            labelsMat.put(i, 0, trainingLabels.get(i));
        }
        recognizer.train(trainingImages, labelsMat);
    }

    @Override
    public Integer recognizeFace(Mat faceInput) {
        if (trainingImages.isEmpty()) return null;

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(faceInput, faces);
        if (faces.empty()) return null;

        Rect faceRect = faces.toArray()[0];
        Mat faceROI = new Mat(faceInput, faceRect);
        Mat grayFace = new Mat();
        Imgproc.cvtColor(faceROI, grayFace, Imgproc.COLOR_BGR2GRAY);
        Mat resized = new Mat();
        Imgproc.resize(grayFace, resized, FACE_SIZE);

        int[] label = new int[1];
        double[] confidence = new double[1];
        recognizer.predict(resized, label, confidence);

        System.out.println("Rozpoznano: label=" + label[0] + ", confidence=" + confidence[0]);
        return confidence[0] < 80.0 ? label[0] : null;
    }

    @Override
    public void saveModel(String filePath) {
        recognizer.save(filePath);
    }

    public List<Mat> getTrainingImages() { return trainingImages; }
    public List<Integer> getTrainingLabels() { return trainingLabels; }
    public Map<Integer, String> getLabelNameMap() { return labelNameMap; }
}
