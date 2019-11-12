#include "PlateLocate.h"

PlateLocate::PlateLocate()
{
}

PlateLocate::~PlateLocate()
{
}

/**
* �ߴ�У��(��߱�&���)
*/
int PlateLocate::verifySizes(RotatedRect rotatedRect)
{
	//�ݴ���
	float error = 0.75f;
	//�����߱�
	float aspect = float(136) / float(36);
	//��ʵ��߱�
	float realAspect = float(rotatedRect.size.width) / float(rotatedRect.size.height);
	if (realAspect < 1) realAspect = (float)rotatedRect.size.height / (float)rotatedRect.size.width;
	//��ʵ���
	float area = rotatedRect.size.height * rotatedRect.size.width;
	//��С ������ �����ϵĶ���
	//������ž��� ��ʱ����
	//��������һЩû��ϵ�� �⻹�ǳ���ɸѡ��
	int areaMin = 44 * aspect * 14;
	int areaMax = 440 * aspect * 140;

	//�������� error��ΪҲ����
	//��С��߱�
	float aspectMin = aspect - aspect * error;
	//����߱�
	float aspectMax = aspect + aspect * error;

	if ((area < areaMin || area > areaMax) || (realAspect < aspectMin || realAspect > aspectMax))
		return 0;
	return 1;
}

/**
* ���ν���
*/
void PlateLocate::tortuosity(Mat src, vector<RotatedRect>& rects, vector<Mat>& dst_plates)
{
	//ѭ��Ҫ����ľ���
	for (RotatedRect roi_rect : rects) {
		//���νǶ�
		float roi_angle = roi_rect.angle;
		float r = (float)roi_rect.size.width / (float)roi_rect.size.height;
		if (r < 1) {
			roi_angle = 90 + roi_angle;
		}

		//���δ�С
		Size roi_rect_size = roi_rect.size;

		//��rect��һ����ȫ�ķ�Χ(���ܳ���src)
		Rect2f  safa_rect;
		safeRect(src, roi_rect, safa_rect);

		//��ѡ����
		//��ͼ  ���ﲻ�ǲ���һ����ͼƬ ������src���϶�λ��һ��Mat �����Ǵ���
		//���ݺ�src��ͬһ��
		Mat src_rect = src(safa_rect);
		//�����ĺ�ѡ����
		Mat dst;
		//����Ҫ��ת�� ��ת�Ƕ�Сû��Ҫ��ת��
		if (roi_angle - 5 < 0 && roi_angle + 5 > 0) {
			dst = src_rect.clone();
		}
		else {
			//�����roi�����ĵ� ����ȥ���Ͻ����������������ͼ��
			//��ȥ���Ͻ���������ں�ѡ���Ƶ����ĵ� ����
			Point2f roi_ref_center = roi_rect.center - safa_rect.tl();
			Mat rotated_mat;
			//���� rotated_mat: �������ͼƬ
			rotation(src_rect, rotated_mat, roi_rect_size, roi_ref_center, roi_angle);
			dst = rotated_mat;
		}

		//������С
		Mat plate_mat;
		//��+��
		plate_mat.create(36, 136, CV_8UC3);
		resize(dst, plate_mat, plate_mat.size());

		dst_plates.push_back(plate_mat);
		dst.release();
	}
}
/**
* ת����ȫ����
*/
void PlateLocate::safeRect(Mat src, RotatedRect rect, Rect2f& safa_rect)
{
	//RotatedRect û������
	//תΪ�����Ĵ�����ı߿�
	Rect2f boudRect = rect.boundingRect2f();

	//���Ͻ� x,y
	float tl_x = boudRect.x > 0 ? boudRect.x : 0;
	float tl_y = boudRect.y > 0 ? boudRect.y : 0;
	//�������� ���� x��y ��0��ʼ�� ����-1
	//���������10��x���������9�� ����src.clos-1 
	//���½�
	float br_x = boudRect.x + boudRect.width < src.cols
		? boudRect.x + boudRect.width - 1
		: src.cols - 1;

	float br_y = boudRect.y + boudRect.height < src.rows
		? boudRect.y + boudRect.height - 1
		: src.rows - 1;

	float  w = br_x - tl_x;
	float h = br_y - tl_y;
	if (w <= 0 || h <= 0) return;
	safa_rect = Rect2f(tl_x, tl_y, w, h);
}
/**
* ��ת
*/
void PlateLocate::rotation(Mat src, Mat& dst, Size rect_size, Point2f center, double angle)
{
	//�����ת����
	Mat rot_mat = getRotationMatrix2D(center, angle, 1);

	//���÷���任
	Mat mat_rotated;
	//������ ��С�᲻һ�������ǶԽ��߿϶�������
	int max = sqrt(pow(src.rows, 2) + pow(src.cols, 2));
	warpAffine(src, mat_rotated, rot_mat, Size(max, max),
		INTER_CUBIC);
	//imshow("��תǰ", src);
	//imshow("��ת��", mat_rotated);
	//��ȡ �����ѳ��ƶ���������ȡ��
	getRectSubPix(mat_rotated, Size(rect_size.width, rect_size.height), center, dst);
	//imshow("��ȡ��", dst);
	//waitKey();

	mat_rotated.release();
	rot_mat.release();
}
