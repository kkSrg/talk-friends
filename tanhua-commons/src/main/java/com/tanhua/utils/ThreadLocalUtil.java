package com.tanhua.utils;

public class ThreadLocalUtil {

    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    //1、向本地线程存储
    public static void setId(Long id){
        threadLocal.set(id);
    }
    //2、从本地线程获取
    public static Long getId(){
        return threadLocal.get();
    }
    //3、清除
    public static void close(){
        threadLocal.remove();
    }
}
