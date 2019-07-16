// LS1.cpp: 定义应用程序的入口点。
//

#include "LS1.h"

using namespace std;

int main() 
{

	int arr[] = { 100,200,300 };
	
	int *p[3];//指针数组

	
	for (int i = 0; i < 3; i++)
	{
		p[i] = &arr[i];

	}
	printf("-------操作后-------\n");
	for (int i = 0; i < 3; i++)
	{
		printf("数组%d\n", *p[i]);
	}
	system("pause");
	return 0;
}
