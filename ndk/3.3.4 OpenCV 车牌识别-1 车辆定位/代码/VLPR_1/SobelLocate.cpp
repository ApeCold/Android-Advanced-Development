#include "SobelLocate.h"

SobelLocate::SobelLocate()
{
}

SobelLocate::~SobelLocate()
{
}
/**
* ���ƶ�λ��1��ԭͼ��2�������ѡ����
*/
void SobelLocate::locate(Mat src, vector<Mat>& dst_plates)
{
	//1����˹ģ��
	Mat blur;
	//ksize: they both must be positive and odd
	//opencv ֻ���������뾶�� �뾶Խ��Խģ��
	GaussianBlur(src, blur, Size(5, 5), 0);
	imshow("ԭͼ", src);
	//imshow("��˹ģ��", blur);
	
	//2,�ҶȻ�
	Mat gray;
	cvtColor(blur, gray, COLOR_BGR2GRAY);
	//imshow("�ҶȻ�", gray);

	//3,Sobel����
	Mat sobel_16;
	//����ͼ����8λ��, uint8
	//Sobel�����󵼺󣬵������ܵ�ֵ�����255��С��0��
	Sobel(gray, sobel_16, CV_16S, 1, 0);
	//imshow("sobel_16", sobel_16);//�޷���ʾ
	//ת��8λ
	Mat sobel;
	convertScaleAbs(sobel_16, sobel);
	//imshow("sobel", sobel);

	//4, ��ֵ�����Ǻڼ��ף�
	Mat shold;
	threshold(sobel, shold,0,255,THRESH_OTSU + THRESH_BINARY);
	//imshow("��ֵ��", shold);

	//5����̬ѧ�������ղ���
	Mat close;
	Mat element = getStructuringElement(MORPH_RECT, Size(17, 3));
	morphologyEx(shold, close, MORPH_CLOSE, element);
	//imshow("�ղ���", close);

	//6��������
	vector<vector<Point>> contours;
	findContours(close, //����ͼ��
		contours, //�������
		RETR_EXTERNAL, //������
		CHAIN_APPROX_NONE	//�������������ص�
	);
	RotatedRect rotatedRect;
	vector<RotatedRect> vec_sobel_rects;
	//7���������жϾ��γߴ�
	for each (vector<Point> points in contours)
	{
		rotatedRect = minAreaRect(points);//���Ƕȵľ���
		//rectangle(src, rotatedRect.boundingRect(), Scalar(0, 0, 255));
		if (verifySizes(rotatedRect)) {
			vec_sobel_rects.push_back(rotatedRect);
		}
	}
	for each (RotatedRect rect in vec_sobel_rects)
	{	
		//rectangle(src, rect.boundingRect(), Scalar(0, 255, 0));
	}
	//imshow("������", src);
	//8�� ���ν������Ƕ��жϣ���ת���Σ�������С��
	tortuosity(src, vec_sobel_rects, dst_plates);
	for each (Mat m in dst_plates)
	{
		imshow("sobel��λ��ѡ����", m);
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
