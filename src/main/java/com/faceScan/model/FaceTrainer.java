package com.faceScan.model;

import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FaceTrainer {

    private static final Size FACE_SIZE = new Size(200, 200);
    private static final String CASCADE_PATH = "/haarcascades/haarcascade_frontalface_alt.xml";

    private static CascadeClassifier loadCascadeFromResources(String resourcePath) {
        try (InputStream is = FaceTrainer.class.getResourceAsStream(resourcePath)) {
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


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCV loaded: " + Core.NATIVE_LIBRARY_NAME);

        String dataPath = "face_data";
        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        CascadeClassifier faceDetector = loadCascadeFromResources(CASCADE_PATH);

        File dataDir = new File(dataPath);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("Brak folderu: " + dataPath);
            return;
        }

        for (File personDir : dataDir.listFiles()) {
            if (personDir.isDirectory()) {
                int label;
                try {
                    label = Integer.parseInt(personDir.getName());
                } catch (NumberFormatException e) {
                    System.err.println("Folder " + personDir.getName() + " nie jest liczbą – pomijam.");
                    continue;
                }

                for (File imgFile : personDir.listFiles()) {
                    if (imgFile.getName().endsWith(".jpg") || imgFile.getName().endsWith(".png")) {
                        Mat img = Imgcodecs.imread(imgFile.getAbsolutePath());
                        if (!img.empty()) {
                            MatOfRect faces = new MatOfRect();
                            faceDetector.detectMultiScale(img, faces);
                            if (!faces.empty()) {
                                Rect faceRect = faces.toArray()[0];
                                Mat faceROI = new Mat(img, faceRect);
                                Mat grayFace = new Mat();
                                Imgproc.cvtColor(faceROI, grayFace, Imgproc.COLOR_BGR2GRAY);
                                Mat resized = new Mat();
                                Imgproc.resize(grayFace, resized, FACE_SIZE);

                                images.add(resized);
                                labels.add(label);
                                System.out.println("Dodano: " + imgFile.getAbsolutePath() + " -> label " + label);
                            } else {
                                System.err.println("Nie wykryto twarzy w: " + imgFile.getAbsolutePath());
                            }
                        } else {
                            System.err.println("Nie można wczytać obrazu: " + imgFile.getAbsolutePath());
                        }
                    }
                }
            }
        }

        if (images.isEmpty()) {
            System.err.println("Brak danych treningowych.");
            return;
        }

        Mat labelsMat = new Mat(labels.size(), 1, CvType.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.put(i, 0, labels.get(i));
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.train(images, labelsMat);
        recognizer.save("trained_model.yml");

        System.out.println("Model wytrenowany i zapisany do: trained_model.yml");

        // ---TEST---

        System.out.println("\n=== TESTOWANIE ===");
        for (File personDir : dataDir.listFiles()) {
            if (personDir.isDirectory()) {
                int expectedLabel;
                try {
                    expectedLabel = Integer.parseInt(personDir.getName());
                } catch (NumberFormatException e) {
                    continue;
                }

                for (File imgFile : personDir.listFiles()) {
                    if (imgFile.getName().endsWith(".jpg") || imgFile.getName().endsWith(".png")) {
                        Mat img = Imgcodecs.imread(imgFile.getAbsolutePath());
                        if (!img.empty()) {
                            MatOfRect faces = new MatOfRect();
                            faceDetector.detectMultiScale(img, faces);
                            if (!faces.empty()) {
                                Rect faceRect = faces.toArray()[0];
                                Mat faceROI = new Mat(img, faceRect);
                                Mat grayFace = new Mat();
                                Imgproc.cvtColor(faceROI, grayFace, Imgproc.COLOR_BGR2GRAY);
                                Mat resized = new Mat();
                                Imgproc.resize(grayFace, resized, FACE_SIZE);

                                int[] predictedLabel = new int[1];
                                double[] confidence = new double[1];
                                recognizer.predict(resized, predictedLabel, confidence);

                                System.out.printf("Obraz: %-30s | Oczekiwano: %2d | Rozpoznano: %2d | Pewność: %.2f%n",
                                        imgFile.getName(), expectedLabel, predictedLabel[0], confidence[0]);
                            } else {
                                System.err.println("Nie wykryto twarzy (TEST): " + imgFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }

        System.out.println("\n=== TESTOWANIE NA NIEWIDZIANYCH ZDJĘCIACH ===");

        String testPath = "face_test";
        File testDir = new File(testPath);
        if (!testDir.exists() || !testDir.isDirectory()) {
            System.err.println("Folder testowy nie istnieje: " + testPath);
            return;
        }

        for (File personDir : testDir.listFiles()) {
            if (personDir.isDirectory()) {
                int expectedLabel;
                try {
                    expectedLabel = Integer.parseInt(personDir.getName());
                } catch (NumberFormatException e) {
                    System.err.println("Folder " + personDir.getName() + " nie jest liczbą – pomijam.");
                    continue;
                }

                for (File imgFile : personDir.listFiles()) {
                    if (imgFile.getName().endsWith(".jpg") || imgFile.getName().endsWith(".png")) {
                        Mat img = Imgcodecs.imread(imgFile.getAbsolutePath());
                        if (!img.empty()) {
                            MatOfRect faces = new MatOfRect();
                            faceDetector.detectMultiScale(img, faces);
                            if (!faces.empty()) {
                                Rect faceRect = faces.toArray()[0];
                                Mat faceROI = new Mat(img, faceRect);
                                Mat grayFace = new Mat();
                                Imgproc.cvtColor(faceROI, grayFace, Imgproc.COLOR_BGR2GRAY);
                                Mat resized = new Mat();
                                Imgproc.resize(grayFace, resized, FACE_SIZE);

                                int[] predictedLabel = new int[1];
                                double[] confidence = new double[1];
                                recognizer.predict(resized, predictedLabel, confidence);

                                System.out.printf("Obraz: %-30s | Oczekiwano: %d | Rozpoznano: %d | Pewność: %.2f\n",
                                        imgFile.getName(), expectedLabel, predictedLabel[0], confidence[0]);
                            } else {
                                System.err.println("Nie wykryto twarzy w: " + imgFile.getAbsolutePath());
                            }
                        } else {
                            System.err.println("Nie można wczytać obrazu: " + imgFile.getAbsolutePath());
                        }
                    }
                }
            }
        }


    }
}