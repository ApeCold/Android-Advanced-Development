#ifndef PLATERECOGNIZE_H
#define PLATERECOGNIZE_H

#include "SobelLocate.h"
#include "ColorLocate.h"
#include "SvmPredict.h"
#include "AnnPredict.h"

class PlateRecognize {
public:
	PlateRecognize(const char* svm_model, const char* ann_model, const char* ann_zh_model);
	~PlateRecognize();

	string recognize(Mat src);
private:
	SobelLocate* sobelLocate = 0;
	ColorLocate* colorLocate = 0;
	SvmPredict* svmPredict = 0;
	AnnPredict* annPredict = 0;
};


#endif // PLATERECOGNIZE_H


