#include "FaceTrack.h"

FaceTrack::FaceTrack(const char *model, const char *seeta) {
    Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(model));
    Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(model));
    DetectionBasedTracker::Parameters detectorParams;
    //追踪器
    tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, detectorParams);
    faceAlignment = makePtr<seeta::FaceAlignment>(seeta);
}
//开启追踪器
void FaceTrack::startTracking() {
    tracker->run();
}

//关闭追踪器
void FaceTrack::stopTracking() {
    tracker->stop();
}


void FaceTrack::detector(Mat src, vector<Rect2f> &rects) {
    vector<Rect> faces;
    //src :灰度图
    tracker->process(src);
    tracker->getObjects(faces);
    if (faces.size()) {
        Rect face = faces[0];
        rects.push_back(Rect2f(face.x, face.y, face.width, face.height));

        //使用faceAlignment 关键点定位
        seeta::ImageData image_data(src.cols, src.rows);
        image_data.data = src.data;

        seeta::FaceInfo faceInfo;
        seeta::Rect bbox;//人脸框信息
        bbox.x = face.x;
        bbox.y = face.y;
        bbox.width = face.width;
        bbox.height = face.height;
        faceInfo.bbox = bbox;

        seeta::FacialLandmark points[5];

        faceAlignment->PointDetectLandmarks(image_data, faceInfo, points);

        //points 转 rect
        for (int i = 0; i < 5; ++i) {
            rects.push_back(Rect2f(points[i].x, points[i].y, 0, 0));
        }

    }
}
