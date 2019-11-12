#include "SvmPredict.h"


SvmPredict::SvmPredict(const char* svm_model)
{
	svm = SVM::load(svm_model);
	
	svmHog = new HOGDescriptor(Size(128, 64), Size(16, 16), Size(8, 8), Size(8, 8), 3);
}

SvmPredict::~SvmPredict()
{
	svm->clear();
	svm.release();
	if (svmHog)
	{
		delete svmHog;
		svmHog = 0;
	}
}

int SvmPredict::doPredict(vector<Mat> candi_plates, Mat &final_plate)
{
	//�����ж�
	Mat plate;
	float score;
	int index = -1;
	float minScore = FLT_MAX;
	for (int i = 0; i < candi_plates.size(); i++)
	{
		plate = candi_plates[i];
		//��ȡ����ͼƬ��������HOG 
		//Ԥ����
		Mat gray;
		cvtColor(plate, gray, COLOR_BGR2GRAY);
		//��ֵ��
		Mat shold;
		//THRESH_OTSU ���ɷ� ����Ӧ��ֵ
		//THRESH_BINARY ����ֵ��
		//THRESH_BINARY_INV ����ֵ��
		threshold(gray, shold, 0, 255, THRESH_OTSU + THRESH_BINARY);

		Mat features;
		getHogFeatures(svmHog, shold, features);

		Mat sample = features.reshape(1, 1);
		score = svm->predict(sample, noArray(), StatModel::Flags::RAW_OUTPUT);
		//printf("svm��ѡ����%d�����ǣ�%f\n", i, score);
		//scoreԽСԽ�����ǳ���
		if (score < minScore) {
			minScore = score;
			index = i;
		}
		gray.release();
		shold.release();
		features.release();
		sample.release();
	}
	if (index >= 0) {
		final_plate = candi_plates[index].clone();
		//imshow("svm�������ճ���", final_plate);
	}


	return index;
}

void SvmPredict::getHogFeatures(HOGDescriptor* hog, Mat src, Mat& dst) {
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

