#ifndef COLORLOCATE_H
#define COLORLOCATE_H

#include "PlateLocate.h"


class ColorLocate : public PlateLocate {
public:
	ColorLocate();
	~ColorLocate();
	/**
	* ���ƶ�λ��1��ԭͼ��2�������ѡ����
	*/
	void locate(Mat src, vector<Mat> &dst_plates);

};


#endif // COLORLOCATE_H


