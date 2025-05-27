package com.faceScan.iface;

import org.opencv.core.Mat;

public interface IFaceRecognizer {
    String recognizeFace(Mat face);
}