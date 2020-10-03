package com.yxhuang.asm;

public class MyClassLoader extends ClassLoader {

    public MyClassLoader() {
    }

    public Class defineClass(String name, byte[] bytes){
        return defineClass(name, bytes, 0, bytes.length);
    }
}
