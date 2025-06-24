package com.faceScan.model;

import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FaceTrainer {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCV loaded: " + Core.NATIVE_LIBRARY_NAME);

        // Ścieżka do folderu ze zdjęciami
        String dataPath = "face_data";

        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        File dataDir = new File(dataPath);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("Brak folderu: " + dataPath);
            return;
        }

        for (File personDir : dataDir.listFiles()) {
            if (personDir.isDirectory()) {
                int label;
                try {
                    label = Integer.parseInt(personDir.getName()); // ← Używamy ID jako labela
                } catch (NumberFormatException e) {
                    System.err.println("Folder " + personDir.getName() + " nie jest liczbą – pomijam.");
                    continue;
                }

                for (File imgFile : personDir.listFiles()) {
                    if (imgFile.getName().endsWith(".jpg") || imgFile.getName().endsWith(".png")) {
                        Mat img = Imgcodecs.imread(imgFile.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                        if (!img.empty()) {
                            images.add(img);
                            labels.add(label);
                            System.out.println("Dodano: " + imgFile.getAbsolutePath() + " -> label " + label);
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

        String outputModelPath = "trained_model.yml";
        recognizer.save(outputModelPath);

        System.out.println("Model wytrenowany i zapisany do: " + outputModelPath);
    }
}
