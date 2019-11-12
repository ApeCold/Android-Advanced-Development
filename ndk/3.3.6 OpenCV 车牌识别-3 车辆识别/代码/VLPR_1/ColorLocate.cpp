#include "ColorLocate.h"

ColorLocate::ColorLocate()
{
}

ColorLocate::~ColorLocate()
{
}

void ColorLocate::locate(Mat src, vector<Mat>& dst_plates)
{
	//1, 预处理
	//src目前是BGR颜色空间，转换成HSV
	Mat hsv;
	cvtColor(src, hsv, COLOR_BGR2HSV);
	//imshow("hsv", hsv);
	//找蓝色像素：h(100-124),s(43-255),v(46-255)
	//遍历
	//获取通道数
	int channels = hsv.channels();
	int height = hsv.rows;
	int width = hsv.cols * channels;

	if (hsv.isContinuous()) {//如果是连续存储，按照1行来处理
		width *= height;
		height = 1;
	}
	uchar* p;
	for (int i = 0; i < height; i++)
	{
		p = hsv.ptr<uchar>(i);//取第i行的数据
		for (int j = 0; j < width; j += channels) {//每次处理channels个数据，这里j步长为channels
			//获取 h s v 分量
			int h = p[j];
			int s = p[j + 1];
			int v = p[j + 2];
			//h(100-124),s(43-255),v(46-255)
			bool isBlue = false;
			if (h >= 100 && h <= 124 &&
				s >= 43 && s <= 255 &&
				v >= 46 && v <= 255
				) {
				isBlue = true;
			}
			if (isBlue)
			{
				//凸显蓝色（v分量最大255）
				p[j] = 0;//h
				p[j + 1] = 0;//s
				p[j + 2] = 255;//v
			}
			else {
				//变黑（v分量为0）
				p[j] = 0;//h
				p[j + 1] = 0;//s
				p[j + 2] = 0;//v
			}
		}
	}// end for
	//imshow("凸显蓝色", hsv);
	vector<Mat> hsv_split;
	split(hsv, hsv_split);//对图像按通道进行分离 ， merge()合并

	//imshow("分离v分量", hsv_split[2]);//v分离

	//-----------下面从sobel定位中拷贝的

	//二值化
	Mat shold;
	//THRESH_OTSU 大律法 自适应阈值
	//THRESH_BINARY 正二值化
	//THRESH_BINARY_INV 反二值化
	//蓝色车牌：字符浅背景深，正二值化
	//黄色车牌：字符深背景浅，反二值化
	threshold(hsv_split[2], shold, 0, 255, THRESH_OTSU + THRESH_BINARY);
	//imshow("color二值化", shold);
	//闭操作
	Mat close;
	Mat element = getStructuringElement(MORPH_RECT, Size(17, 3));
	morphologyEx(shold, close, MORPH_CLOSE, element);
	//imshow("color闭操作", close);

	//找轮廓
	vector<vector<Point>> contours;
	findContours(close, //输入图像
		contours, //输出轮廓
		RETR_EXTERNAL, //外轮廓
		CHAIN_APPROX_NONE	//轮廓上所有像素点
	);
	RotatedRect rotatedRect;
	vector<RotatedRect> vec_color_rects;
	//遍历并判断矩形尺寸
	Mat src_clone = src.clone();
	for each (vector<Point> points in contours)
	{
		rotatedRect = minAreaRect(points);//带角度的矩形
		rectangle(src_clone, rotatedRect.boundingRect(), Scalar(0, 0, 255));
		if (verifySizes(rotatedRect)) {
			vec_color_rects.push_back(rotatedRect);
		}
	}
	for each (RotatedRect rect in vec_color_rects)
	{
		rectangle(src_clone, rect.boundingRect(), Scalar(0, 255, 0));
	}
	//imshow("color找轮廓", src_clone);
	//矩形矫正（角度判断，旋转矩形，调整大小）
	tortuosity(src, vec_color_rects, dst_plates);
	/*for each (Mat m in dst_plates)
	{
		//imshow("color定位候选车牌", m);
		//waitKey();
	}*/

	hsv.release();
	for (Mat m :hsv_split) {
		m.release();
	}
	shold.release();
	close.release();
	element.release();
	src_clone.release();

}
