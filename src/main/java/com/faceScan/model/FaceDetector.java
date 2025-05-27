package com.faceScan.model;

import com.faceScan.iface.IFaceDetector;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector implements IFaceDetector {

    private final CascadeClassifier faceCascade;

    public FaceDetector() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.faceCascade = new CascadeClassifier("src/main/resources/haarcascade_frontalface_default.xml");
    }

    @Override
    public Mat detectFace(Mat frame){
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frame, faces);

        for(Rect rect : faces.toArray()){
            return new Mat(frame,rect);
        }
        return null;
    }
}