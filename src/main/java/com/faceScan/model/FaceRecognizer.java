package com.faceScan.model;

import com.faceScan.iface.IFaceRecognizer;
import org.opencv.core.Mat;
import org.opencv.face.LBPHFaceRecognizer;

public class FaceRecognizer implements IFaceRecognizer {

    @Override
    public String recognizeFace(Mat face) {
        // wersja testowa
        return "Rozpoznano: Kylian Mbappe";
    }
}
