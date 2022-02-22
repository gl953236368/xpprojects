package com.px.xpinline.model;

public class InitContextModel {
    // 单例
    private static InitContextModel initContextModel = null;
    private ClassLoader classLoader;
    public InitContextModel() {}

    public static InitContextModel newInstance() {
        if(initContextModel == null){
            synchronized(InitContextModel.class){
                while (initContextModel == null){
                    initContextModel = new InitContextModel();
                }
            }
        }
        return initContextModel;
    }


    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
