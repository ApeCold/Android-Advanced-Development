#include "tool.h"

int find_max(int arr[], int n){
	int max = arr[0];
	int i;  
	for(i=0; i<n; i++){
		if(arr[i] > max){
			max = arr[i];
		}
	}
	return max;
}

