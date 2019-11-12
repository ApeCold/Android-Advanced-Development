#include "PlateRecognize.h"

PlateRecognize::PlateRecognize(const char* svm_model)
{
	sobelLocate = new SobelLocate();
	colorLocate = new ColorLocate();
	svmPredict = new SvmPredict(svm_model);
}

PlateRecognize::~PlateRecognize()
{
	if (sobelLocate)
	{
		delete sobelLocate;
		sobelLocate = 0;
	}
	if (colorLocate)
	{
		delete colorLocate;
		colorLocate = 0;
	}
	if (svmPredict)
	{
		delete svmPredict;
		svmPredict = 0;
	}
}

/**
* 车牌识别：车牌定位+检测+字符识别
*/
string PlateRecognize::recognize(Mat src)
{
	//1, 车牌定位
	// sobel 定位
	vector<Mat> sobel_plates;//sobel定位得到的候选车牌
	sobelLocate->locate(src, sobel_plates);
	// color 定位
	vector<Mat> color_plates;//颜色定位得到的候选车牌
	colorLocate->locate(src, color_plates);

	//2合1
	vector<Mat> plates;
	plates.insert(plates.end(), sobel_plates.begin(), sobel_plates.end());
	plates.insert(plates.end(), color_plates.begin(), color_plates.end());
	for (Mat m : sobel_plates) {
		m.release();
	}
	for (Mat m : color_plates) {
		m.release();
	}

	char windowName[100];
	for (size_t i = 0; i < plates.size(); i++)
	{
		sprintf(windowName,"%zd 候选车牌", i);
		imshow(windowName, plates[i]);
		//waitKey();
	}

	//候选车牌里是不是有两类：车牌和非车牌
	//对候选车牌进行精选， SVM：用来分类的
	Mat plate;
	//找到最可能是车牌的图片
	int index = svmPredict->doPredict(plates, plate);

	//获取到了一个最可能是车牌的图
	//对这个图片进行字符识别

	waitKey();
	return string("1234");
}
