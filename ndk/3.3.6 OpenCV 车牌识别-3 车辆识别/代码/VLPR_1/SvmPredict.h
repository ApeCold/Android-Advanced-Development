
#ifndef SVMPREDICT_H// ctrl + shift + U
#define SVMPREDICT_H

#include <opencv2/opencv.hpp>
#include <string>
//»úÆ÷Ñ§Ï° machine learning

using namespace std;
using namespace cv;
using namespace ml;

class SvmPredict {
public:
	SvmPredict(const char* svm_model);
	~SvmPredict();

	/**
	* svmÆÀ²â
	*/
	int doPredict(vector<Mat> candi_plates, Mat &plate);

private:
	Ptr<SVM> svm;
	HOGDescriptor *svmHog = 0;

	void getHogFeatures(HOGDescriptor *hog, Mat src, Mat &dst);
};


#endif // SVMPREDICT_H


