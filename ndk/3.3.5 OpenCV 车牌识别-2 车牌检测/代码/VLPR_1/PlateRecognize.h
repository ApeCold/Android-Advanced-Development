#ifndef PLATERECOGNIZE_H
#define PLATERECOGNIZE_H

#include "SobelLocate.h"
#include "ColorLocate.h"
#include "SvmPredict.h"

class PlateRecognize {
public:
	PlateRecognize(const char* svm_model);
	~PlateRecognize();

	string recognize(Mat src);
private:
	SobelLocate* sobelLocate = 0;
	ColorLocate* colorLocate = 0;
	SvmPredict* svmPredict = 0;
};


#endif // PLATERECOGNIZE_H


