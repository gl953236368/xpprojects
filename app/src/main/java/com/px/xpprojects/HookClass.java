package com.px.xpprojects;

public class HookClass {
    public String a = "测试参数a";
    public String b = "测试参数b";
    private int c = 1;

    public HookClass(){

    }

    public static String getResult(String a){
        return "这就是测试结果:" + a;
    }

    public String getResult(String a, String b){
        return "这就是测试结果:" + a + b;
    }

    public int getResult(){
        return 100 + this.c;
    }


}
