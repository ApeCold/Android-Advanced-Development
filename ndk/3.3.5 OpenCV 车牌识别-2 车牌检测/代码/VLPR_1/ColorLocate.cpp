#include "ColorLocate.h"

ColorLocate::ColorLocate()
{
}

ColorLocate::~ColorLocate()
{
}

void ColorLocate::locate(Mat src, vector<Mat>& dst_plates)
{
	//1, Ԥ����
	//srcĿǰ��BGR��ɫ�ռ䣬ת����HSV
	Mat hsv;
	cvtColor(src, hsv, COLOR_BGR2HSV);
	//imshow("hsv", hsv);
	//����ɫ���أ�h(100-124),s(43-255),v(46-255)
	//����
	//��ȡͨ����
	int channels = hsv.channels();
	int height = hsv.rows;
	int width = hsv.cols * channels;

	if (hsv.isContinuous()) {//����������洢������1��������
		width *= height;
		height = 1;
	}
	uchar* p;
	for (int i = 0; i < height; i++)
	{
		p = hsv.ptr<uchar>(i);//ȡ��i�е�����
		for (int j = 0; j < width; j += channels) {//ÿ�δ���channels�����ݣ�����j����Ϊchannels
			//��ȡ h s v ����
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
				//͹����ɫ��v�������255��
				p[j] = 0;//h
				p[j + 1] = 0;//s
				p[j + 2] = 255;//v
			}
			else {
				//��ڣ�v����Ϊ0��
				p[j] = 0;//h
				p[j + 1] = 0;//s
				p[j + 2] = 0;//v
			}
		}
	}// end for
	//imshow("͹����ɫ", hsv);
	vector<Mat> hsv_split;
	split(hsv, hsv_split);//��ͼ��ͨ�����з��� �� merge()�ϲ�

	//imshow("����v����", hsv_split[2]);//v����

	//-----------�����sobel��λ�п�����

	//��ֵ��
	Mat shold;
	//THRESH_OTSU ���ɷ� ����Ӧ��ֵ
	//THRESH_BINARY ����ֵ��
	//THRESH_BINARY_INV ����ֵ��
	//��ɫ���ƣ��ַ�ǳ���������ֵ��
	//��ɫ���ƣ��ַ����ǳ������ֵ��
	threshold(hsv_split[2], shold, 0, 255, THRESH_OTSU + THRESH_BINARY);
	//imshow("color��ֵ��", shold);
	//�ղ���
	Mat close;
	Mat element = getStructuringElement(MORPH_RECT, Size(17, 3));
	morphologyEx(shold, close, MORPH_CLOSE, element);
	//imshow("color�ղ���", close);

	//������
	vector<vector<Point>> contours;
	findContours(close, //����ͼ��
		contours, //�������
		RETR_EXTERNAL, //������
		CHAIN_APPROX_NONE	//�������������ص�
	);
	RotatedRect rotatedRect;
	vector<RotatedRect> vec_color_rects;
	//�������жϾ��γߴ�
	Mat src_clone = src.clone();
	for each (vector<Point> points in contours)
	{
		rotatedRect = minAreaRect(points);//���Ƕȵľ���
		rectangle(src_clone, rotatedRect.boundingRect(), Scalar(0, 0, 255));
		if (verifySizes(rotatedRect)) {
			vec_color_rects.push_back(rotatedRect);
		}
	}
	for each (RotatedRect rect in vec_color_rects)
	{
		rectangle(src_clone, rect.boundingRect(), Scalar(0, 255, 0));
	}
	//imshow("color������", src_clone);
	//���ν������Ƕ��жϣ���ת���Σ�������С��
	tortuosity(src, vec_color_rects, dst_plates);
	/*for each (Mat m in dst_plates)
	{
		//imshow("color��λ��ѡ����", m);
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
