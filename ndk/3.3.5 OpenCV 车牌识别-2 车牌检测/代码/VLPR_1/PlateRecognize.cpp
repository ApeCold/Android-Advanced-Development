#include "PlateRecognize.h"

PlateRecognize::PlateRecognize(const char* svm_model)
{
	sobelLocate = new SobelLocate();
	colorLocate = new ColorLocate();
	svmPredict = new SvmPredict(svm_model);
}

PlateRecognize::~PlateRecognize()
{
	if (sobelLocate)
	{
		delete sobelLocate;
		sobelLocate = 0;
	}
	if (colorLocate)
	{
		delete colorLocate;
		colorLocate = 0;
	}
	if (svmPredict)
	{
		delete svmPredict;
		svmPredict = 0;
	}
}

/**
* ����ʶ�𣺳��ƶ�λ+���+�ַ�ʶ��
*/
string PlateRecognize::recognize(Mat src)
{
	//1, ���ƶ�λ
	// sobel ��λ
	vector<Mat> sobel_plates;//sobel��λ�õ��ĺ�ѡ����
	sobelLocate->locate(src, sobel_plates);
	// color ��λ
	vector<Mat> color_plates;//��ɫ��λ�õ��ĺ�ѡ����
	colorLocate->locate(src, color_plates);

	//2��1
	vector<Mat> plates;
	plates.insert(plates.end(), sobel_plates.begin(), sobel_plates.end());
	plates.insert(plates.end(), color_plates.begin(), color_plates.end());
	for (Mat m : sobel_plates) {
		m.release();
	}
	for (Mat m : color_plates) {
		m.release();
	}

	char windowName[100];
	for (size_t i = 0; i < plates.size(); i++)
	{
		sprintf(windowName,"%zd ��ѡ����", i);
		imshow(windowName, plates[i]);
		//waitKey();
	}

	//��ѡ�������ǲ��������ࣺ���ƺͷǳ���
	//�Ժ�ѡ���ƽ��о�ѡ�� SVM�����������
	Mat plate;
	//�ҵ�������ǳ��Ƶ�ͼƬ
	int index = svmPredict->doPredict(plates, plate);

	//��ȡ����һ��������ǳ��Ƶ�ͼ
	//�����ͼƬ�����ַ�ʶ��

	waitKey();
	return string("1234");
}
