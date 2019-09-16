package com.netease.architect.adapter;

import com.netease.architect.adapter.reader.BReader;
import com.netease.architect.adapter.reader.ReaderAdapter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderUnitTest {

//    @Test
//    public BufferedReader getReader(File file) throws IOException {
//        // 文件字节流
//        FileInputStream fis = new FileInputStream(file);
//
//        // 字节读取流
//        InputStreamReader isr = new InputStreamReader(fis);
//
//        // 缓冲字节流
//        return new BufferedReader(isr);
//    }

    @Test
    public void reader() throws IOException {
        FileInputStream fis = new FileInputStream(new File("c:/netease.txt"));
        BReader bReader = new ReaderAdapter(new ISReaderImpl(fis));

        BufferedReader reader = bReader.getBReader();
        System.out.println(reader.readLine());
    }
}
