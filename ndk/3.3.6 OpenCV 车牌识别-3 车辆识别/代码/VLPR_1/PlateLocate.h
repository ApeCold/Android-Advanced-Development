#ifndef PLATELOCATE_H
#define PLATELOCATE_H

#include <opencv2/opencv.hpp>
#include <string>

using namespace std;
using namespace cv;


class PlateLocate {
public:
	PlateLocate();
	virtual ~PlateLocate();
	
	/**
	* 车牌定位：1，原图；2，输出候选车牌
	*/
	virtual void locate(Mat src, vector<Mat> &dst_plates) = 0;

protected:
	int verifySizes(RotatedRect rotatedRect);
	void tortuosity(Mat src, vector<RotatedRect> &rects, vector<Mat> &dst_plates);
	void safeRect(Mat src, RotatedRect rect, Rect2f &safa_rect);
	void rotation(Mat src, Mat &dst, Size rect_size, Point2f center, double angle);
};


#endif // PLATELOCATE_H


