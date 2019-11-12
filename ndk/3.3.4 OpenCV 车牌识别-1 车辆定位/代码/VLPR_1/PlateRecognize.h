#ifndef PLATERECOGNIZE_H
#define PLATERECOGNIZE_H

#include "SobelLocate.h"

class PlateRecognize {
public:
	PlateRecognize();
	~PlateRecognize();

	string recognize(Mat src);
private:
	SobelLocate* sobelLocate = 0;
};


#endif // PLATERECOGNIZE_H


