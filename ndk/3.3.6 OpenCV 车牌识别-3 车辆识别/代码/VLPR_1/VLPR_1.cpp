// VLPR : vehicle license plate recognizer
//
//

#include "PlateRecognize.h"


int main()
{
	//imread 读取图片
	Mat src = imread("C:/Users/Administrator/Desktop/vlpr/test/test8.jpg");
	//imshow("src", src);
	PlateRecognize pr("C:/Users/Administrator/Desktop/vlpr/train/svm/svm.xml",
		"C:/Users/Administrator/Desktop/vlpr/train/ann/ann.xml",
		"C:/Users/Administrator/Desktop/vlpr/train/ann/ann_zh.xml"
	
	);
	string str_plate = pr.recognize(src);
	cout << "车牌：" << str_plate << endl;
	waitKey();
	return 0;
}
