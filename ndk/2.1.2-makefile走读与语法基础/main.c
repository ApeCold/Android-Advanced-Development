#include <stdio.h>
#include "tool.h"

int main(){
	//printf("hello world\n");
	int arr[] = {1, 3, 5, 8, 2};
	int max = find_max(arr, 5);
	printf("max = %d\n", max);
	return 0;
}