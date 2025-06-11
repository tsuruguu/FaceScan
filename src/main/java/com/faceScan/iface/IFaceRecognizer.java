package com.faceScan.iface;

import org.opencv.core.Mat;

public interface IFaceRecognizer {
    void clearTraining();

    void addTrainingSample(int label, String imagePath);

    void trainModel();

    Integer recognizeFace(Mat face);

    void saveModel(String filePath);
}
