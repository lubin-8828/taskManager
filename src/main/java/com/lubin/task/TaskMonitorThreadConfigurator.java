package com.lubin.task;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池，可配置
 */
public class TaskMonitorThreadConfigurator {
    private static int corePoolSize = 14;
    private static int maximumPoolSize = 28;
    private static long keepAliveTime = 60;
    private static volatile ThreadPoolExecutor EXECUTOR;

    /**
     * 自定义线程池参数，注意仅在未调用submitTask方法前有效
     * @param coreSize 核心数
     * @param maximumSize 最大数
     * @param keepAlive 空闲存活时间 秒
     */
    public static void configThread(int coreSize, int maximumSize, long keepAlive) {
        TaskMonitorThreadConfigurator.corePoolSize = coreSize;
        TaskMonitorThreadConfigurator.maximumPoolSize = maximumSize;
        TaskMonitorThreadConfigurator.keepAliveTime = keepAlive;
    }

    /**
     * 执行任务
     * @param runnable runnable
     */
    public static void submitTask(Runnable runnable) {
        if (EXECUTOR == null) {
            synchronized (TaskMonitorThreadConfigurator.class) {
                if (EXECUTOR == null) {
                    init();
                }
            }
        }
        EXECUTOR.execute(runnable);
    }

    /**
     * 初始化线程池
     */
    private static void init() {
        EXECUTOR = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
    }

    /**
     * 动态设置核心线程数，谨慎使用
     * @param corePoolSize corePoolSize
     */
    public static void setCorePoolSize(int corePoolSize) {
        TaskMonitorThreadConfigurator.corePoolSize = corePoolSize;
        if (EXECUTOR != null) {
            EXECUTOR.setCorePoolSize(TaskMonitorThreadConfigurator.corePoolSize);
        }
    }

    /**
     * 动态设置最大线程数，谨慎使用
     * @param maximumPoolSize maximumPoolSize
     */
    public static void setMaximumPoolSize(int maximumPoolSize) {
        TaskMonitorThreadConfigurator.maximumPoolSize = maximumPoolSize;
        if (EXECUTOR != null) {
            EXECUTOR.setMaximumPoolSize(TaskMonitorThreadConfigurator.maximumPoolSize);
        }
    }

    /**
     * 动态设置线程空闲存活时间
     * @param keepAliveTime keepAliveTime
     */
    public static void setKeepAliveTime(long keepAliveTime) {
        TaskMonitorThreadConfigurator.keepAliveTime = keepAliveTime;
        if (EXECUTOR != null) {
            EXECUTOR.setKeepAliveTime(TaskMonitorThreadConfigurator.keepAliveTime, TimeUnit.SECONDS);
        }
    }
}
