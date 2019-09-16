package com.netease.architect.adapter;

import com.netease.architect.adapter.reader.ISReader;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ISReaderImpl implements ISReader {

    private InputStream is;

    public ISReaderImpl(InputStream is) {
        this.is = is;
    }

    @Override
    public InputStreamReader getISReader() {
        return new InputStreamReader(is);
    }
}
