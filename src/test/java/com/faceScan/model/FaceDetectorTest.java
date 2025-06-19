package com.faceScan.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import static org.junit.jupiter.api.Assertions.*;

class FaceDetectorTest {

    @BeforeAll
    static void loadLibrary() {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    void testCascadeIsLoaded() {
        FaceDetector detector = new FaceDetector();
        assertNotNull(detector.getFaceCascade());
        assertFalse(detector.getFaceCascade().empty());
    }

    @Test
    void testDetectFaceOnEmptyMatDoesNotThrow() {
        FaceDetector detector = new FaceDetector();
        Mat empty = new Mat();
        Mat result = detector.detectFace(empty);

        assertNotNull(result);
        assertEquals(empty.size(), result.size());
    }

    @Test
    void testDetectFaceFromImage() {
        String imagePath = "src/test/resources/test_face.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        assertFalse(image.empty(), "Obrazek testowy nie został wczytany!");

        FaceDetector detector = new FaceDetector();
        Mat result = detector.detectFace(image);

        assertNotNull(result);
        assertEquals(image.size(), result.size());

        MatOfRect faces = new MatOfRect();
        detector.getFaceCascade().detectMultiScale(image, faces);
        Rect[] detected = faces.toArray();

        System.out.println("Wykryto twarzy: " + detected.length);
        assertTrue(detected.length >= 1, "Nie wykryto żadnej twarzy!");
    }

    @Test
    void testDetectExactlyOneFace() {
        String imagePath = "src/test/resources/one_face.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        assertFalse(image.empty(), "Obraz testowy nie został wczytany!");

        FaceDetector detector = new FaceDetector();
        MatOfRect faces = new MatOfRect();
        detector.getFaceCascade().detectMultiScale(image, faces);

        Rect[] detected = faces.toArray();
        System.out.println("Liczba wykrytych twarzy: " + detected.length);

        assertEquals(1, detected.length, "Oczekiwano dokładnie jednej twarzy!");
    }
}
