## 类型转换

>除了能使用c语言的强制类型转换外,还有：转换操作符 （新式转换）

### const_cast

>修改类型的const或volatile属性 

```c++
const char *a;
char *b = const_cast<char*>(a);
	
char *a;
const char *b = const_cast<const char*>(a);
```

### static_cast

>1. 基础类型之间互转。如：float转成int、int转成unsigned int等
>2. 指针与void之间互转。如：float\*转成void\*、Bean\*转成void\*、函数指针转成void\*等
>3. 子类指针/引用与 父类指针/引用 转换。

```c++
class Parent {
public:
	void test() {
		cout << "p" << endl;
	}
};
class Child :public Parent{
public:
	 void test() {
		cout << "c" << endl;
	}
};
Parent  *p = new Parent;
Child  *c = static_cast<Child*>(p);
//输出c
c->test();

//Parent test加上 virtual 输出 p
```

### dynamic_cast

> 主要 将基类指针、引用 安全地转为派生类.
>
> 在运行期对可疑的转型操作进行安全检查，仅对多态有效

```c++
//基类至少有一个虚函数
//对指针转换失败的得到NULL，对引用失败  抛出bad_cast异常 
Parent  *p = new Parent;
Child  *c = dynamic_cast<Child*>(p);
if (!c) {
	cout << "转换失败" << endl;
}


Parent  *p = new Child;
Child  *c = dynamic_cast<Child*>(p);
if (c) {
	cout << "转换成功" << endl;
}
```

### reinterpret_cast 

> 对指针、引用进行原始转换

```c++
float i = 10;

//&i float指针，指向一个地址，转换为int类型，j就是这个地址
int j = reinterpret_cast<int>(&i);
cout  << hex << &i << endl;
cout  << hex  << j << endl;

cout<<hex<<i<<endl; //输出十六进制数
cout<<oct<<i<<endl; //输出八进制数
cout<<dec<<i<<endl; //输出十进制数
```

### char*与int转换

```c++
//char* 转int float
int i = atoi("1");
float f = atof("1.1f");
cout << i << endl;
cout << f << endl;
	
//int 转 char*
char c[10];
//10进制
itoa(100, c,10);
cout << c << endl;

//int 转 char*
char c1[10];
sprintf(c1, "%d", 100);
cout << c1 << endl;
```



## 异常

```c++
void test1()
{
	throw "测试!";
}

void test2()
{
	throw exception("测试");
}

try {
	test1();
}
catch (const char *m) {
	cout << m << endl;
}
try {
	test2();
}
catch (exception  &e) {
	cout << e.what() << endl;
}

//自定义
class MyException : public exception
{
public:
   virtual char const* what() const
    {
        return "myexception";
    }
};

//随便抛出一个对象都可以
```





## 文件与流操作

> C 语言的文件读写操作
>
> 头文件:stdio.h
>
> 函数原型：FILE * fopen(const char * path, const char * mode); 
>
> path:  操作的文件路径
>
> mode:模式

| 模式 | 描述                                                         |
| ---- | ------------------------------------------------------------ |
| r    | 打开一个已有的文本文件，允许读取文件。                       |
| w    | 打开一个文本文件，允许写入文件。如果文件不存在，则会创建一个新文件。在这里，您的程序会从文件的开头写入内容。如果文件存在，则该会被截断为零长度，重新写入。 |
| a    | 打开一个文本文件，以追加模式写入文件。如果文件不存在，则会创建一个新文件。在这里，您的程序会在已有的文件内容中追加内容。 |
| r+   | 打开一个文本文件，允许读写文件。                             |
| w+   | 打开一个文本文件，允许读写文件。如果文件已存在，则文件会被截断为零长度，如果文件不存在，则会创建一个新文件。 |
| a+   | 打开一个文本文件，允许读写文件。如果文件不存在，则会创建一个新文件。读取会从文件的开头开始，写入则只能是追加模式。 |

```C++
//========================================================================
FILE *f = fopen("xxxx\\t.txt","w");
//写入单个字符
fputc('a', f);
fclose(f);


FILE *f = fopen("xxxx\\t.txt","w");
char *txt = "123456";
//写入以 null 结尾的字符数组
fputs(txt, f);
//格式化并输出
fprintf(f,"%s",txt);
fclose(f);

//========================================================================
fgetc(f); //读取一个字符

char buff[255];
FILE *f = fopen("xxxx\\t.txt", "r");
//读取 遇到第一个空格字符停止
fscanf(f, "%s", buff);
printf("1: %s\n", buff);

//最大读取 255-1 个字符
fgets(buff, 255, f);
printf("2: %s\n", buff);
fclose(f);

//二进制 I/O 函数
size_t fread(void *ptr, size_t size_of_elements, 
             size_t number_of_elements, FILE *a_file);       
size_t fwrite(const void *ptr, size_t size_of_elements, 
             size_t number_of_elements, FILE *a_file);
//1、写入/读取数据缓存区
//2、每个数据项的大小
//3、多少个数据项
//4、流
//如：图片、视频等以二进制操作:
//写入buffer 有 1024个字节
fwrite(buffer,1024,1,f);
```



> C++ 文件读写操作
>
> \<iostream\> 和 \<fstream\>

| 数据类型 | 描述                                               |
| -------- | -------------------------------------------------- |
| ofstream | 输出文件流，创建文件并向文件写入信息。             |
| ifstream | 输入文件流，从文件读取信息。                       |
| fstream  | 文件流，且同时具有 ofstream 和 ifstream 两种功能。 |

```c++
char data[100];
// 以写模式打开文件
ofstream outfile;
outfile.open("XXX\\f.txt");
cout << "输入你的名字: ";
//cin 接收终端的输入
cin >> data;
// 向文件写入用户输入的数据
outfile << data << endl;
// 关闭打开的文件
outfile.close();

// 以读模式打开文件
ifstream infile;
infile.open("XXX\\f.txt");

cout << "读取文件" << endl;
infile >> data;
cout << data << endl;

// 关闭
infile.close();
```

