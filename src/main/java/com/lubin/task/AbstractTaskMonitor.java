package com.lubin.task;

import lombok.extern.slf4j.Slf4j;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

/**
 * 抽象任务监控器，负责任务的创建、执行和监控
 *
 * @param <DataType> 任务结果数据类型
 * @param <TaskContext> 任务上下文类型
 * @param <Task> 具体任务类型
 */
@Slf4j
public abstract class AbstractTaskMonitor<DataType, TaskContext, Task extends AbstractTask<TaskContext, DataType>> {
    private CountDownLatch latch;
    private int totalTasks = 0;
    private boolean isStarted = false;
    private int monitorFinish = 0;
    private final TaskDataCarrier<DataType> taskDataCarrier = new TaskDataCarrier<>();

    /**
     * 子类实现此方法来创建具体的任务
     * 返回结果是任务编号和任务体的映射关系，用来满足任务结果有序排列的场景
     *
     * @return 任务映射表
     * @throws Exception 任务创建过程中的异常
     */
    protected abstract TreeMap<Integer, Task> createTasks() throws Exception;

    /**
     * 启动所有任务并等待完成
     *
     * @throws Exception 任务执行过程中的异常
     */
    public final void execute() throws Exception {
        log.info("Starting task monitor");
        TreeMap<Integer, Task> tasks = this.createTasks();
        this.totalTasks = tasks.size();
        log.info("Created {} tasks", totalTasks);
        
        latch = new CountDownLatch(totalTasks);
        for (int serial : tasks.keySet()) {
            AbstractTask<TaskContext, DataType> task = tasks.get(serial);
            task.setLatch(latch);
            task.setSerial(serial);
            task.setDataCarrier(this.taskDataCarrier);
            TaskMonitorThreadConfigurator.submitTask(task);
            log.debug("Submitted task {}", serial);
        }
        
        isStarted = true;
        log.info("Waiting for all tasks to complete");
        latch.await();
        this.monitorFinish = 1;
        log.info("All tasks completed");
    }

    /**
     * 判断是否执行中
     * @return 是否
     */
    public final boolean isFinish() {
        return this.isStarted && (this.latch.getCount() + this.monitorFinish) == (this.totalTasks + 1);
    }

    /**
     * 获取总体进度
     *
     * @return 完成进度（0.0-1.0）
     */
    public final double getProgress() {
        if (!isStarted || totalTasks == 0) {
            return 0.0;
        }
        long remainingTasks = latch.getCount() + this.monitorFinish;
        long completedTasks = totalTasks + 1 - remainingTasks;
        return (double) completedTasks / (totalTasks + 1);
    }

    public final int getTotalTasks() {
        return totalTasks;
    }

    /**
     * 获取数据载体
     *
     * @return 数据载体对象
     */
    public final TaskDataCarrier<DataType> getDataCarrier() {
        return taskDataCarrier;
    }

    public abstract void afterExecute();
}
