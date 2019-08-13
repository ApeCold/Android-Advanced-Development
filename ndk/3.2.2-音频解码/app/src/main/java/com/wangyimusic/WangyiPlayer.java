package com.dongnao.wangyimusic;

public class WangyiPlayer {
    static {
        System.loadLibrary("wangyiplayer");
    }

//    input.mp3    out.pcm
    public native void sound(String input,String output);
}
