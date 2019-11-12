
#ifndef ANNPREDICT_H// ctrl + shift + U
#define ANNPREDICT_H

#include <opencv2/opencv.hpp>
#include <string>
//机器学习 machine learning

using namespace std;
using namespace cv;
using namespace ml;

class AnnPredict {
public:
	AnnPredict(const char* ann_model, const char* ann_zh_model);
	~AnnPredict();

	/**
	* ann评测
	*/
	string doPredict(Mat plate);

private:
	Ptr<ANN_MLP> ann;//数字+字母的
	Ptr<ANN_MLP> ann_zh;//中文的
	HOGDescriptor* annHog = 0;

	void getHogFeatures(HOGDescriptor* hog, Mat src, Mat& dst);
	bool clearMaoDing(Mat &plate);
	bool verifyCharSize(Mat plate);
	int getCityIndex(vector<Rect> vec_ann_rects);
	void getChineseRect(Rect city, Rect &chineseRect);
	void predict(vector<Mat> vec, string &result);
	static string ZHCHARS[];
	static char CHARS[];
};


#endif // ANNPREDICT_H


