// LS1.cpp: 定义应用程序的入口点。
//

#include "LS1.h"

using namespace std;

struct Student
{
	short j;
	int i;
	short k;

} s1,s2;


int main() 
{
	struct Student student;
	student.i = 10;
	student.j = 5;

	s1.i = 10;
	s1.j = 5;
	printf("结构体大小%d   ", sizeof(student)); 
	system("pause");
	return 0;
}
