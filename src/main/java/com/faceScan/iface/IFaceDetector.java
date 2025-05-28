package com.faceScan.iface;

import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

public interface IFaceDetector {
    Mat detectFace(Mat frame);

    CascadeClassifier getFaceCascade();
}