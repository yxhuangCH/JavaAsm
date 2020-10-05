package com.yxhuang.asm;

/**
 * Created by yxhuang
 * Date: 2020/9/7
 * Description:
 */
public class MyMain implements Mesurable {
    public int a = 0;
    public int b = 1;
    public int abc = 2;

    public void test01(){
        int origin = 9;
        int result = abc - origin;
        System.out.println("origin print result=" + result);
    }
    public void test02(){

    }
}
