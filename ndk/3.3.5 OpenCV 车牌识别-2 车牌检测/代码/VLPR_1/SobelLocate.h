#ifndef SOBELLOCATE_H
#define SOBELLOCATE_H

#include "PlateLocate.h"


class SobelLocate : public PlateLocate{
public:
	SobelLocate();
	~SobelLocate();
	/**
	* ���ƶ�λ��1��ԭͼ��2�������ѡ����
	*/
	void locate(Mat src, vector<Mat>& dst_plates);

};


#endif // SOBELLOCATE_H


