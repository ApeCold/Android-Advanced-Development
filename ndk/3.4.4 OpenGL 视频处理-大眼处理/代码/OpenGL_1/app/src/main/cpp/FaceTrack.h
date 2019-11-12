#ifndef FACETRACK_H
#define FACETRACK_H

#include <opencv2/opencv.hpp>
#include <opencv2/objdetect.hpp>
#include <vector>
#include "FaceAlignment/include/face_alignment.h"


using namespace std;
using namespace cv;

class CascadeDetectorAdapter : public DetectionBasedTracker::IDetector {
public:
    CascadeDetectorAdapter(Ptr<CascadeClassifier> detector) :
            IDetector(),
            Detector(detector) {
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects) {
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize,
                                   maxObjSize);
    }

    virtual ~CascadeDetectorAdapter() {
    }

private:
    CascadeDetectorAdapter();

    Ptr<CascadeClassifier> Detector;
};


class FaceTrack {
public:
    // model: opencv的模型
    // seeta：seeta的模型
    FaceTrack(const char *model, const char *seeta);

    void detector(Mat src, vector<Rect2f> &rects);

    void startTracking();

    void stopTracking();

private:
    Ptr<DetectionBasedTracker> tracker;
    Ptr<seeta::FaceAlignment> faceAlignment;
};

#endif //FACETRACK_H
