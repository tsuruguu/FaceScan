package com.faceScan.model;

import com.faceScan.iface.IFaceRecognizer;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceRecognizer implements IFaceRecognizer {
    private LBPHFaceRecognizer recognizer;
    private final List<Mat>       trainingImages  = new ArrayList<>();
    private final List<Integer>   trainingLabels  = new ArrayList<>();
    private final Map<Integer,String> labelNameMap = new HashMap<>();

    public FaceRecognizer() {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        this.recognizer = LBPHFaceRecognizer.create();
    }

    @Override
    public void clearTraining() {
        trainingImages.clear();
        trainingLabels.clear();
        labelNameMap.clear();
        recognizer = LBPHFaceRecognizer.create();
    }

    @Override
    public void addTrainingSample(int label, String imagePath) {
        Mat img = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
        if (!img.empty()) {
            trainingImages.add(img);
            trainingLabels.add(label);
            labelNameMap.putIfAbsent(label, imagePath);
        }
    }

    @Override
    public void trainModel() {
        if (trainingImages.isEmpty()) return;
        MatOfInt labelsMat = new MatOfInt();
        labelsMat.fromList(trainingLabels);
        recognizer.train(trainingImages, labelsMat);
    }

    @Override
    public Integer recognizeFace(Mat face) {
        if (trainingImages.isEmpty()) return null;
        int[] labelArr = new int[1];
        double[] confidenceArr = new double[1];
        Mat grayFace = new Mat();
        if (face.channels() > 1) {
            Imgproc.cvtColor(face, grayFace, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayFace = face;
        }
        recognizer.predict(grayFace, labelArr, confidenceArr);
        if (confidenceArr[0] < 80.0) {
            return labelArr[0];
        }
        return null;
    }


    @Override
    public void saveModel(String filePath) {
        recognizer.write(filePath);
    }

    public List<Mat> getTrainingImages() {return trainingImages;}
    public List<Integer> getTrainingLabels() {return trainingLabels;}
    public Map<Integer, String> getLabelNameMap() {return labelNameMap;}

}
