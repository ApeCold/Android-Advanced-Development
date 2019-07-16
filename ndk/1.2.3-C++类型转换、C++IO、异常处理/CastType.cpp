#define _CRT_SECURE_NO_DEPRECATE；
#define _CRT_SECURE_NO_WARNINGS；
// CastType.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//

#include <iostream>

int main()
{
	 
 
    std::cout << "Hello World!\n"; 
	FILE*myFile;
	FIL E* f2 = fopen_s(&myFile,"G:\\wangyi.txt", "w");
	fprintf(f2, "i am wangyi %d", 88);
}

