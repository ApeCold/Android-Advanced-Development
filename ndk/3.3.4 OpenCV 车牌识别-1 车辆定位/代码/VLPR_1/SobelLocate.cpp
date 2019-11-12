#include "SobelLocate.h"

SobelLocate::SobelLocate()
{
}

SobelLocate::~SobelLocate()
{
}
/**
* 车牌定位：1，原图；2，输出候选车牌
*/
void SobelLocate::locate(Mat src, vector<Mat>& dst_plates)
{
	//1，高斯模糊
	Mat blur;
	//ksize: they both must be positive and odd
	//opencv 只接受奇数半径， 半径越大越模糊
	GaussianBlur(src, blur, Size(5, 5), 0);
	imshow("原图", src);
	//imshow("高斯模糊", blur);
	
	//2,灰度化
	Mat gray;
	cvtColor(blur, gray, COLOR_BGR2GRAY);
	//imshow("灰度化", gray);

	//3,Sobel运算
	Mat sobel_16;
	//输入图像是8位的, uint8
	//Sobel函数求导后，导数可能的值会大于255或小于0，
	Sobel(gray, sobel_16, CV_16S, 1, 0);
	//imshow("sobel_16", sobel_16);//无法显示
	//转回8位
	Mat sobel;
	convertScaleAbs(sobel_16, sobel);
	//imshow("sobel", sobel);

	//4, 二值化（非黑即白）
	Mat shold;
	threshold(sobel, shold,0,255,THRESH_OTSU + THRESH_BINARY);
	//imshow("二值化", shold);

	//5，形态学操作：闭操作
	Mat close;
	Mat element = getStructuringElement(MORPH_RECT, Size(17, 3));
	morphologyEx(shold, close, MORPH_CLOSE, element);
	//imshow("闭操作", close);

	//6，找轮廓
	vector<vector<Point>> contours;
	findContours(close, //输入图像
		contours, //输出轮廓
		RETR_EXTERNAL, //外轮廓
		CHAIN_APPROX_NONE	//轮廓上所有像素点
	);
	RotatedRect rotatedRect;
	vector<RotatedRect> vec_sobel_rects;
	//7，遍历并判断矩形尺寸
	for each (vector<Point> points in contours)
	{
		rotatedRect = minAreaRect(points);//带角度的矩形
		//rectangle(src, rotatedRect.boundingRect(), Scalar(0, 0, 255));
		if (verifySizes(rotatedRect)) {
			vec_sobel_rects.push_back(rotatedRect);
		}
	}
	for each (RotatedRect rect in vec_sobel_rects)
	{	
		//rectangle(src, rect.boundingRect(), Scalar(0, 255, 0));
	}
	//imshow("找轮廓", src);
	//8， 矩形矫正（角度判断，旋转矩形，调整大小）
	tortuosity(src, vec_sobel_rects, dst_plates);
	for each (Mat m in dst_plates)
	{
		imshow("sobel定位候选车牌", m);
		waitKey();
	}

	waitKey();


	blur.release();
	gray.release();
	sobel_16.release();
	sobel.release();
	shold.release();
	close.release();
}
