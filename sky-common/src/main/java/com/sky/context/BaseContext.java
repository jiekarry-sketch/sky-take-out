package com.sky.context;

public class BaseContext {

    //ThreadLocal 是 Java 中提供线程局部变量的一个工具类。
    // 它可以让每个线程拥有自己独立的变量副本，线程之间互不干扰。
    //给每个线程分配了一个小的存储空间/、
    //***在同一线程的不同方法间传递数据***
    //用户请求 → JWT拦截器 → 解析JWT获取员工ID → 存入ThreadLocal → Controller/Service可以随时获取id
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
