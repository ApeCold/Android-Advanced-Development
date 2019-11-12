#include "PlateRecognize.h"

PlateRecognize::PlateRecognize()
{
	sobelLocate = new SobelLocate();
}

PlateRecognize::~PlateRecognize()
{
	if (sobelLocate)
	{
		delete sobelLocate;
		sobelLocate = 0;
	}
}

/**
* 车牌识别：车牌定位+检测+字符识别
*/
string PlateRecognize::recognize(Mat src)
{
	//1, 车牌定位
	// sobel 定位
	vector<Mat> dst_plates;
	sobelLocate->locate(src, dst_plates);

	return string("1234");
}
