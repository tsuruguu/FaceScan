package com.faceScan.iface;

import org.opencv.core.Mat;

public interface IFaceDetector {
    Mat detectFace(Mat frame);
}