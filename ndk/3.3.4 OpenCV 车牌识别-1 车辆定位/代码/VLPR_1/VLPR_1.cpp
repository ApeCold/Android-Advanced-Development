// VLPR : vehicle license plate recognizer
//
//

#include "PlateRecognize.h"


int main()
{
	//imread 读取图片
	Mat src = imread("C:/Users/Administrator/Desktop/车牌识别/资料/Test/test2.jpg");
	//imshow("src", src);
	PlateRecognize pr;
	string str_plate = pr.recognize(src);
	cout << "车牌：" << str_plate << endl;
	waitKey();
	return 0;
}
