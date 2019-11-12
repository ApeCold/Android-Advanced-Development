#include "AnnPredict.h"
string AnnPredict::ZHCHARS[] = { "川", "鄂", "赣", "甘", "贵", "桂", "黑", "沪", "冀", "津", "京", "吉", "辽", "鲁", "蒙", "闽", "宁", "青", "琼", "陕", "苏", "晋", "皖", "湘", "新", "豫", "渝", "粤", "云", "藏", "浙" };
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
	//预处理
	Mat gray;
	cvtColor(plate, gray, COLOR_BGR2GRAY);
	Mat shold;
	//THRESH_OTSU 大律法 自适应阈值
	//THRESH_BINARY 正二值化
	//THRESH_BINARY_INV 反二值化
	threshold(gray, shold, 0, 255, THRESH_OTSU + THRESH_BINARY);
	//imshow("ann二值化", shold);

	if (!clearMaoDing(shold)) {
		return string("未识别到车牌");
	}
	//imshow("ann去铆钉", shold);
	//字符分割
	//找轮廓
	vector<vector<Point>> contours;
	findContours(shold, //输入图像
		contours, //输出轮廓
		RETR_EXTERNAL, //外轮廓
		CHAIN_APPROX_NONE	//轮廓上所有像素点
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
	//imshow("ann所有轮廓", src_clone);

	/*for each (Rect rect in vec_ann_rects)
	{
		imshow("ann字符", src_clone(rect));
		waitKey();
	}*/

	//对字符矩形轮廓排序，从左到右
	sort(vec_ann_rects.begin(), vec_ann_rects.end(), [](const Rect& rect1, const Rect& rect2) {
		return rect1.x < rect2.x;
	});
	//获取城市字符轮廓的索引
	int cityIndex = getCityIndex(vec_ann_rects);
	//推导汉字字符的轮廓
	Rect chineseRect;
	getChineseRect(vec_ann_rects[cityIndex], chineseRect);

	vector<Mat> plateCharMats;
	plateCharMats.push_back(shold(chineseRect));
	//取汉字后的6个字符
	int count = 6;
	if (vec_ann_rects.size() < 6) {
		return string("未识别到车牌");
	}
	for (int i = cityIndex; i < vec_ann_rects.size() && count; i++, count--)
	{
		plateCharMats.push_back(shold(vec_ann_rects[i]));
	}

	//字符识别
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
	//归一化处理
	Mat trainImg = Mat(hog->winSize, CV_32S);
	resize(src, trainImg, hog->winSize);

	//计算特征
	vector<float> desc;
	hog->compute(trainImg, desc, hog->winSize);

	Mat feature(desc);

	feature.copyTo(dst);

	feature.release();
	trainImg.release();
}

//字符行：最小跳变次数12；最大跳变次数：12+8*6=60
//计算每一行像素的跳变次数，
//如果小于指定最大阈值，认为该行是铆钉行，将该行像素值全部改为0，涂黑
bool AnnPredict::clearMaoDing(Mat& plate)
{
	int maxChangeCount = 12;//铆钉行最大跳变次数阈值，可以调节
	vector<int> changes;//统计每行的跳变次数
	for (int i = 0; i < plate.rows; i++)
	{
		int changeCount = 0;//记录第i行的跳变次数
		for (int j = 0; j < plate.cols - 1; j++)
		{
			char pixel = plate.at<char>(i, j);//前一个像素
			char pixel_1 = plate.at<char>(i, j + 1);//后一个像素
			if (pixel != pixel_1) {//像素不等，跳变次数+1
				changeCount++;
			}
		}
		changes.push_back(changeCount);
	}

	//车牌字符跳变条件
	//满足字符像素行跳变条件的行数 == 字符的高度
	int charRows = 0;//记录满足字符像素行跳变条件的行数
	for (int i = 0; i < plate.rows; i++)
	{
		if (changes[i] >= 12 && changes[i] <= 60) {
			charRows++;
		}
	}
	//判断字符高度占整个车牌高度的百分比
	float heightPercent = charRows * 1.0/ plate.rows;
	//printf("车牌字符高度百分比： %f\n", heightPercent);

	if (heightPercent <= 0.4) {
		return false;
	}

	//判断字符区域的面积占车牌面积的百分比
	int plate_area = plate.rows * plate.cols;//所有像素点个数
	//字符区域=白点像素的个数
	//第1种方式: 循环遍历判断像素值255
	//第2种方式：countNonZero(plate)
	float areaPercent = countNonZero(plate) * 1.0 / plate_area;
	//printf("车牌字符面积百分比： %f\n", areaPercent);
	if (areaPercent <= 0.15 || areaPercent >= 0.5) {
		return false;
	}

	for (int i = 0; i < changes.size(); i++)//plate.rows
	{
		int changeCount = changes[i];
		if (changeCount < maxChangeCount) {
			//将该行像素值全部改为0，涂黑
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
	//最理想情况 车牌字符的标准宽高比
	float aspect = 45.0f / 90.0f;
	// 当前获得矩形的真实宽高比
	float realAspect = (float)src.cols / (float)src.rows;
	//最小的字符高
	float minHeight = 10.0f;
	//最大的字符高
	float maxHeight = 35.0f;
	//1、判断高符合范围  2、宽、高比符合范围
	//最大宽、高比 最小宽高比
	float error = 0.7f;
	float maxAspect = aspect + aspect * error;//0.85
	float minAspect = 0.05f;

	int plate_area = src.cols * src.rows;
	float areaPercent = countNonZero(src) * 1.0 / plate_area;

	printf("单个字符面积占比:%f\n", areaPercent);
	printf("单个字符宽高比:%f\n", realAspect);
	printf("单个字符高:%d\n", src.rows);

	if (areaPercent <= 0.8 && realAspect >= minAspect && realAspect <= maxAspect 
		&& src.rows >= minHeight &&
		src.rows <= maxHeight) {
		return 1;
	}
	return 0;
}
//找城市轮廓索引
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
	//把宽度稍微扩大一点
	float width = city.width * 1.15f;
	//城市轮廓的x坐标
	int x = city.x;

	//x ：当前汉字后面城市轮廓的x坐标
	//减去城市的宽
	int newX = x - width;
	chineseRect.x = newX >= 0 ? newX : 0;
	chineseRect.y = city.y;
	chineseRect.width = width;
	chineseRect.height = city.height;
}

void AnnPredict::predict(vector<Mat> vec, string& result) {
	for (int i = 0; i < vec.size(); ++i) {
		Mat plate_char = vec[i];
		//提取HOG特征
		Mat features;
		getHogFeatures(annHog, plate_char, features);

		Mat sample = features.reshape(1, 1);
		Mat response;
		Point maxLoc;
		Point minLoc;
		if (i) {//?
			//字母和数字
			ann->predict(sample, response);
			minMaxLoc(response, 0, 0, &minLoc, &maxLoc);
			int index = maxLoc.x;//训练时的 索引，跟CHARS对应
			result += CHARS[index];
		}
		else {
			//汉字
			ann_zh->predict(sample, response);
			minMaxLoc(response, 0, 0, &minLoc, &maxLoc);
			int index = maxLoc.x;
			result += ZHCHARS[index];
		}
	}
}