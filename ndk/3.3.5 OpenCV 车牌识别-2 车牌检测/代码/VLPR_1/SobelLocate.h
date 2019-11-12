#ifndef SOBELLOCATE_H
#define SOBELLOCATE_H

#include "PlateLocate.h"


class SobelLocate : public PlateLocate{
public:
	SobelLocate();
	~SobelLocate();
	/**
	* 车牌定位：1，原图；2，输出候选车牌
	*/
	void locate(Mat src, vector<Mat>& dst_plates);

};


#endif // SOBELLOCATE_H


