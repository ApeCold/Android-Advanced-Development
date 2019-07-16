
// LS3Method.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//
#include <iostream>
#include "fuc.h"

void (*funcp)(int* a, int* b);
void point_func(int *a,int *b)
{
	*a = 200;
	printf("函数指针\n");
}

int* int_add_func(void* wParam)
{
	printf("指针函数\n");
	int b = 10;
	int *p = &b;
	return p;
}

int main()
{
	int a = 10;
	int_add_func(&a);
	int b = 20;
	funcp = point_func;
	funcp(&a, &b);
	printf("a值 %d",a);

}

int func(void) {
	printf("函数");
	return -1;
}
int func(void* pathName, int a) {
	printf("函数");
	return -1;
}