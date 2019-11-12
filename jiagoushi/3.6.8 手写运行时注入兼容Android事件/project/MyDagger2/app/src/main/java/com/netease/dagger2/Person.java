package com.netease.dagger2;

public class Person {

    Student student;

    public Person(Student student) {
        this.student = student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    //test method
    public static void main(String[] args) {
        Person person = new Person(new Student(99));
    }
}
