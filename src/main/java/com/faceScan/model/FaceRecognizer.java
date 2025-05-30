package com.faceScan.model;

import com.faceScan.iface.IFaceRecognizer;
import org.opencv.core.Mat;
import org.opencv.face.LBPHFaceRecognizer;

import java.util.HashMap;
import java.util.Map;

public class FaceRecognizer implements IFaceRecognizer {
    private final LBPHFaceRecognizer recognizer;
    private final Map<Integer, String> labelNameMap;

    public FaceRecognizer() {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

        this.recognizer=LBPHFaceRecognizer.create();
        this.recognizer.read("trained_model.yml");

        this.labelNameMap=new HashMap<>();
        this.labelNameMap.put(1, "<UNK>");
//        this.labelNameMap.put(2, "<UNK>");
    }

    @Override
    public String recognizeFace(Mat face) {
        if(face==null) return null;

        int[] label=new int[1];
        double[] confidence=new double[1];

        recognizer.predict(face, label, confidence);

        if(confidence[0]<80.0) return labelNameMap.getOrDefault(label[0], "<UNK>");

        return null;
    }
}