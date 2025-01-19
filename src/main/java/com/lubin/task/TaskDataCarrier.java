package com.lubin.task;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据载体类，用于存储和管理任务执行结果
 * 使用TreeMap保证结果的有序性，使用ReentrantLock确保线程安全
 *
 * @param <DataType> 数据类型
 */
@Slf4j
public class TaskDataCarrier<DataType> {
    /**
     * 锁，防止存放数据错乱
     */
    private final ReentrantLock lock = new ReentrantLock(true);
    
    /**
     * 结果集合，key为任务序号，value为任务结果
     */
    private final TreeMap<Integer, TaskData<DataType>> results = new TreeMap<>();

    /**
     * 存放结果
     *
     * @param runNum 序号
     * @param result 结果
     */
    public final void putResult(Integer runNum, TaskData<DataType> result) {
        try {
            log.debug("Storing result for task {}", runNum);
            lock.lock();
            results.put(runNum, result);
            log.debug("Stored result for task {}, success: {}", runNum, result.isSuccess());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 按顺序返回结果集合
     *
     * @return 有序的结果集合
     */
    public final Collection<TaskData<DataType>> getData() {
        log.debug("Retrieving all results, total count: {}", results.size());
        return results.values();
    }
}
