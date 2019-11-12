package com.netease.rxjavastudy.fx;

import java.util.ArrayList;
import java.util.List;

public class TestClient<T> {

    public void test() {
        T t = null;
        t.hashCode();  // 能够调用到Object里面的方法
    }

    public static void main(String[] args) {

        /*List list = new ArrayList();
        list.add("A");
        list.add(1);
        list.add(6.7);
        Object o = list.get(1); // 1  运行时，类型转换异常
        String s = (String) o; */

        // 泛型出现后
        List<String> list = new ArrayList();
        list.add("A");
        // list.add(6); // 编译期 就可以看到错误
        String s = list.get(0);


        // --------
        /*Test<Worker> test = null;
        test.add(new Worker()); // 只能传递Worker*/


        //  下面时 上限 和 下限 的测试


        // todo 上限
        // show1(new Test<Object>()); // Person的父类，会报错
        show1(new Test<Person>());
        show1(new Test<Student>());
        show1(new Test<Worker>());
        show1(new Test<WorkerStub>());

        // todo 下限
        // show2(new Test<WorkerStub>()); // 因为最低限制的子类 Student，不能在低，不能是Student的子类了
        // show2(new Test<StudentStub>()); // 因为最低限制的子类 Student，不能在低，不能是Student的子类了
        show2(new Test<Student>());
        show2(new Test<Person>()); // 父类
        show2(new Test<Object>()); // 父类


        // todo 读写模式

        // todo 可读模式
        /*Test<? extends Person> test1 = null;
        test1.add(new Person()); // 不可写
        test1.add(new Student()); // 不可写
        test1.add(new Object()); // 不可写
        test1.add(new Worker()); // 不可写
        Person person = test1.get(); // 可读*/

        // todo 可写模式  不完全可读
        Test<? super Person> test = null;
        test.add(new Person()); // 可写
        test.add(new Student()); // 可写
        test.add(new Worker()); // 可写

        Object object = test.get(); // 不完全可读

    }

    /**
     * extends 上限  Person or Person的所有子类 都可以， 最高的类型只能是Person，把最高的类型给限制住了
     * @param test
     * @param <T>
     */
    public static <T> void show1(Test<? extends Person> test) {

    }

    /**
     * extends 下限  Student or Student 的所有父类 都可以  最低的类型只能是Student，把最低的类型给限制住了
     * @param test
     * @param <T>
     */
    public static <T> void show2(Test<? super Student> test) {

    }
}
