package com.faceScan.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class FaceRecognizerTest {

    @BeforeAll
    static void loadOpenCV() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    void testAddTrainingSample() {
        FaceRecognizer recognizer = new FaceRecognizer();
        String testImagePath = "src/test/resources/one_face.jpg";

        recognizer.addTrainingSample(1, testImagePath);

        assertFalse(recognizer.getTrainingImages().isEmpty(), "Obraz treningowy nie został dodany");
        assertFalse(recognizer.getTrainingLabels().isEmpty(), "Etykieta nie została dodana");
        assertTrue(recognizer.getLabelNameMap().containsKey(1), "Mapa etykiet nie zawiera ID 1");
    }

    @Test
    void testTrainWithoutSamplesDoesNotCrash() {
        FaceRecognizer recognizer = new FaceRecognizer();
        assertDoesNotThrow(recognizer::trainModel);
    }

    @Test
    void testClearTrainingClearsAllData() {
        FaceRecognizer recognizer = new FaceRecognizer();
        recognizer.addTrainingSample(1, "src/test/resources/one_face.jpg");

        recognizer.clearTraining();

        assertTrue(recognizer.getTrainingImages().isEmpty());
        assertTrue(recognizer.getTrainingLabels().isEmpty());
        assertTrue(recognizer.getLabelNameMap().isEmpty());
    }

    @Test
    void testSaveModelCreatesFile() {
        FaceRecognizer recognizer = new FaceRecognizer();
        recognizer.addTrainingSample(1, "src/test/resources/one_face.jpg");
        recognizer.trainModel();

        String path = "test_model.yml";
        recognizer.saveModel(path);

        File modelFile = new File(path);
        assertTrue(modelFile.exists(), "Plik modelu nie został zapisany");
        modelFile.delete();
    }

    @Test
    void testRecognizeTrainedFace() {
        FaceRecognizer recognizer = new FaceRecognizer();

        int label = 1;
        String[] trainingPaths = {
                "src/test/resources/KR1.jpg",
                "src/test/resources/KR1b.jpg",
                "src/test/resources/KR1c.jpg",
                "src/test/resources/KR1d.jpg",
                "src/test/resources/KR1e.jpg"
        };

        for (String path : trainingPaths) {
            recognizer.addTrainingSample(label, path);
        }

        recognizer.trainModel();

        Mat testImage = Imgcodecs.imread("src/test/resources/KR1.jpg");
        assertFalse(testImage.empty(), "Nie wczytano obrazu testowego!");

        Integer predictedLabel = recognizer.recognizeFace(testImage);
        System.out.println("Predykcja: " + predictedLabel);

        assertNotNull(predictedLabel, "Twarz nie została rozpoznana!");
        assertEquals(label, predictedLabel, "Przewidziany label nie zgadza się z oczekiwanym!");
    }
}
