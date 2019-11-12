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
* ����ʶ�𣺳��ƶ�λ+���+�ַ�ʶ��
*/
string PlateRecognize::recognize(Mat src)
{
	//1, ���ƶ�λ
	// sobel ��λ
	vector<Mat> dst_plates;
	sobelLocate->locate(src, dst_plates);

	return string("1234");
}
