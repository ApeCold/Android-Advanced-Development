#include "AnnPredict.h"
string AnnPredict::ZHCHARS[] = { "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "³", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "ԥ", "��", "��", "��", "��", "��" };
char AnnPredict::CHARS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
AnnPredict::AnnPredict(const char* ann_model, const char* ann_zh_model)
{
	ann = ANN_MLP::load(ann_model);
	ann_zh = ANN_MLP::load(ann_zh_model);
	annHog = new HOGDescriptor(Size(32, 32), Size(16, 16), Size(8, 8), Size(8, 8), 3);
}

AnnPredict::~AnnPredict()
{
	ann->clear();
	ann.release();
	ann_zh->clear();
	ann_zh.release();
	if (annHog)
	{
		delete annHog;
		annHog = 0;
	}
}

string AnnPredict::doPredict(Mat plate)
{
	//Ԥ����
	Mat gray;
	cvtColor(plate, gray, COLOR_BGR2GRAY);
	Mat shold;
	//THRESH_OTSU ���ɷ� ����Ӧ��ֵ
	//THRESH_BINARY ����ֵ��
	//THRESH_BINARY_INV ����ֵ��
	threshold(gray, shold, 0, 255, THRESH_OTSU + THRESH_BINARY);
	//imshow("ann��ֵ��", shold);

	if (!clearMaoDing(shold)) {
		return string("δʶ�𵽳���");
	}
	//imshow("annȥí��", shold);
	//�ַ��ָ�
	//������
	vector<vector<Point>> contours;
	findContours(shold, //����ͼ��
		contours, //�������
		RETR_EXTERNAL, //������
		CHAIN_APPROX_NONE	//�������������ص�
	);
	vector<Rect> vec_ann_rects;
	Mat src_clone = plate.clone();
	for each (vector<Point> points in contours)
	{
		Rect rect = boundingRect(points);
		//rectangle(src_clone, rect, Scalar(0, 0, 255));
		Mat roi = shold(rect);
		if (verifyCharSize(roi)) {
			vec_ann_rects.push_back(rect);
		}
	}
	//imshow("ann��������", src_clone);

	/*for each (Rect rect in vec_ann_rects)
	{
		imshow("ann�ַ�", src_clone(rect));
		waitKey();
	}*/

	//���ַ������������򣬴�����
	sort(vec_ann_rects.begin(), vec_ann_rects.end(), [](const Rect& rect1, const Rect& rect2) {
		return rect1.x < rect2.x;
	});
	//��ȡ�����ַ�����������
	int cityIndex = getCityIndex(vec_ann_rects);
	//�Ƶ������ַ�������
	Rect chineseRect;
	getChineseRect(vec_ann_rects[cityIndex], chineseRect);

	vector<Mat> plateCharMats;
	plateCharMats.push_back(shold(chineseRect));
	//ȡ���ֺ��6���ַ�
	int count = 6;
	if (vec_ann_rects.size() < 6) {
		return string("δʶ�𵽳���");
	}
	for (int i = cityIndex; i < vec_ann_rects.size() && count; i++, count--)
	{
		plateCharMats.push_back(shold(vec_ann_rects[i]));
	}

	//�ַ�ʶ��
	string str_plate;
	predict(plateCharMats, str_plate);
	for (Mat m : plateCharMats) {
		m.release();
	}
	gray.release();
	shold.release();
	src_clone.release();
	return str_plate;
}

void AnnPredict::getHogFeatures(HOGDescriptor* hog, Mat src, Mat& dst)
{
	//��һ������
	Mat trainImg = Mat(hog->winSize, CV_32S);
	resize(src, trainImg, hog->winSize);

	//��������
	vector<float> desc;
	hog->compute(trainImg, desc, hog->winSize);

	Mat feature(desc);

	feature.copyTo(dst);

	feature.release();
	trainImg.release();
}

//�ַ��У���С�������12��������������12+8*6=60
//����ÿһ�����ص����������
//���С��ָ�������ֵ����Ϊ������í���У�����������ֵȫ����Ϊ0��Ϳ��
bool AnnPredict::clearMaoDing(Mat& plate)
{
	int maxChangeCount = 12;//í����������������ֵ�����Ե���
	vector<int> changes;//ͳ��ÿ�е��������
	for (int i = 0; i < plate.rows; i++)
	{
		int changeCount = 0;//��¼��i�е��������
		for (int j = 0; j < plate.cols - 1; j++)
		{
			char pixel = plate.at<char>(i, j);//ǰһ������
			char pixel_1 = plate.at<char>(i, j + 1);//��һ������
			if (pixel != pixel_1) {//���ز��ȣ��������+1
				changeCount++;
			}
		}
		changes.push_back(changeCount);
	}

	//�����ַ���������
	//�����ַ��������������������� == �ַ��ĸ߶�
	int charRows = 0;//��¼�����ַ���������������������
	for (int i = 0; i < plate.rows; i++)
	{
		if (changes[i] >= 12 && changes[i] <= 60) {
			charRows++;
		}
	}
	//�ж��ַ��߶�ռ�������Ƹ߶ȵİٷֱ�
	float heightPercent = charRows * 1.0/ plate.rows;
	//printf("�����ַ��߶Ȱٷֱȣ� %f\n", heightPercent);

	if (heightPercent <= 0.4) {
		return false;
	}

	//�ж��ַ���������ռ��������İٷֱ�
	int plate_area = plate.rows * plate.cols;//�������ص����
	//�ַ�����=�׵����صĸ���
	//��1�ַ�ʽ: ѭ�������ж�����ֵ255
	//��2�ַ�ʽ��countNonZero(plate)
	float areaPercent = countNonZero(plate) * 1.0 / plate_area;
	//printf("�����ַ�����ٷֱȣ� %f\n", areaPercent);
	if (areaPercent <= 0.15 || areaPercent >= 0.5) {
		return false;
	}

	for (int i = 0; i < changes.size(); i++)//plate.rows
	{
		int changeCount = changes[i];
		if (changeCount < maxChangeCount) {
			//����������ֵȫ����Ϊ0��Ϳ��
			for (int j = 0; j < plate.cols; j++)
			{
				plate.at<char>(i, j) = 0;
			}
		}
	}
	return true;
}

bool AnnPredict::verifyCharSize(Mat src)
{
	//��������� �����ַ��ı�׼��߱�
	float aspect = 45.0f / 90.0f;
	// ��ǰ��þ��ε���ʵ��߱�
	float realAspect = (float)src.cols / (float)src.rows;
	//��С���ַ���
	float minHeight = 10.0f;
	//�����ַ���
	float maxHeight = 35.0f;
	//1���жϸ߷��Ϸ�Χ  2�����߱ȷ��Ϸ�Χ
	//�����߱� ��С��߱�
	float error = 0.7f;
	float maxAspect = aspect + aspect * error;//0.85
	float minAspect = 0.05f;

	int plate_area = src.cols * src.rows;
	float areaPercent = countNonZero(src) * 1.0 / plate_area;

	printf("�����ַ����ռ��:%f\n", areaPercent);
	printf("�����ַ���߱�:%f\n", realAspect);
	printf("�����ַ���:%d\n", src.rows);

	if (areaPercent <= 0.8 && realAspect >= minAspect && realAspect <= maxAspect 
		&& src.rows >= minHeight &&
		src.rows <= maxHeight) {
		return 1;
	}
	return 0;
}
//�ҳ�����������
int AnnPredict::getCityIndex(vector<Rect> rects)
{
	int cityIndex = 0;
	for (int i = 0; i < rects.size(); ++i) {
		Rect rect = rects[i];
		int midX = rect.x + rect.width / 2;
		if (midX < 136 / 7 * 2 && midX > 136 / 7) {
			cityIndex = i;
			break;
		}
	}
	return cityIndex;
}

void AnnPredict::getChineseRect(Rect city, Rect &chineseRect) {
	//�ѿ����΢����һ��
	float width = city.width * 1.15f;
	//����������x����
	int x = city.x;

	//x ����ǰ���ֺ������������x����
	//��ȥ���еĿ�
	int newX = x - width;
	chineseRect.x = newX >= 0 ? newX : 0;
	chineseRect.y = city.y;
	chineseRect.width = width;
	chineseRect.height = city.height;
}

void AnnPredict::predict(vector<Mat> vec, string& result) {
	for (int i = 0; i < vec.size(); ++i) {
		Mat plate_char = vec[i];
		//��ȡHOG����
		Mat features;
		getHogFeatures(annHog, plate_char, features);

		Mat sample = features.reshape(1, 1);
		Mat response;
		Point maxLoc;
		Point minLoc;
		if (i) {//?
			//��ĸ������
			ann->predict(sample, response);
			minMaxLoc(response, 0, 0, &minLoc, &maxLoc);
			int index = maxLoc.x;//ѵ��ʱ�� ��������CHARS��Ӧ
			result += CHARS[index];
		}
		else {
			//����
			ann_zh->predict(sample, response);
			minMaxLoc(response, 0, 0, &minLoc, &maxLoc);
			int index = maxLoc.x;
			result += ZHCHARS[index];
		}
	}
}