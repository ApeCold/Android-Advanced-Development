// OpenCV_Face.cpp: 定义应用程序的入口点。
//

#include "OpenCV_Face.h"

int main()
{
	//D:/opencv - 4.1.1/build/etc/haarcascades/haarcascade_frontalface_alt.xml

	//级联分类器
	
	//隐式转换

#ifdef DETECT
	//人脸检测
	if (!face_CascadeClassifier.load("D:/opencv-4.1.1/build/etc/haarcascades/haarcascade_frontalface_alt.xml")) {
		cout << "级联分类器加载失败！\n" << endl;
		return -1;
	}
#else
	//人脸跟踪
	//创建1个主检测适配器
	cv::Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
		makePtr<CascadeClassifier>("D:/opencv-4.1.1/build/etc/haarcascades/haarcascade_frontalface_alt.xml"));
	//创建1个跟踪检测适配器
	cv::Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
		makePtr<CascadeClassifier>("D:/opencv-4.1.1/build/etc/haarcascades/haarcascade_frontalface_alt.xml"));
	//创建跟踪器
	DetectionBasedTracker::Parameters DetectorParams;
	tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
	tracker->run();
#endif // DETECT

	VideoCapture capture;
	capture.open(0);
	if (!capture.isOpened())
	{
		cout << "opencv打开摄像头失败！\n" << endl;
		return -1;
	}
	Mat frame;//摄像头彩色图像
	Mat gray;//摄像头灰度图像
	while (true)
	{
		capture >> frame;
		if (frame.empty()) {
			cout << "opencv读取摄像头图像失败！\n" << endl;
			return -1;
		}
		//读取成功
		//imshow("摄像头", frame);//显示图像
		//灰度化处理
		cvtColor(frame, gray, COLOR_BGR2GRAY);//opencv 中是 B G R
		//imshow("灰度化", gray);//显示图像
		//直方图均衡化，达到目的：增强对比度
		equalizeHist(gray, gray);
		//gray是均衡化后的了

		std::vector<Rect>  faces;
#ifdef DETECT
		face_CascadeClassifier.detectMultiScale(gray, faces);
#else
		tracker->process(gray);
		tracker->getObjects(faces);
#endif // DETECT
		for each (Rect face in faces)
		{
			//1, 在哪个上面画
			//2，人脸框矩形
			//3，画的矩形框的颜色
			rectangle(frame, face, Scalar(0, 0, 255));//蓝B  绿G  红R
			//这种方式来检测相机实时人脸图像非常卡顿！只适合静态图像的检测
#ifdef COLECT_SAMPLES //采集样本
			Mat sample;
			frame(face).copyTo(sample);
			resize(sample, sample, Size(24, 24));//统一大小
			cvtColor(sample, sample, COLOR_BGR2GRAY);
			char p[100];
			sprintf(p, "C:/Users/Administrator/Desktop/opencv/train/face/pos/%d.jpg", i++);
			//imread 读取文件图像
			imwrite(p, sample);//将Mat写入文件
#endif
		}
		imshow("摄像头", frame);//显示图像
		if (waitKey(30) == 27)//Esc键退出
		{
			break;
		}
	}
#ifndef DETECT
	tracker->stop();
#endif // DETECT
	return 0;
}
