package com.lubin.task;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 抽象任务基类，提供任务执行的基本框架
 *
 * @param <TaskContext> 任务上下文类型
 * @param <DataType> 任务结果数据类型
 */
@Slf4j
public abstract class AbstractTask<TaskContext, DataType> implements Runnable {
    private CountDownLatch latch;
    private TaskDataCarrier<DataType> taskDataCarrier;
    protected final TaskContext context;
    private Integer serial;

    /**
     * 构造函数
     *
     * @param context 任务上下文
     */
    public AbstractTask(TaskContext context) {
        this.context = context;
    }

    @Override
    public final void run() {
        log.debug("Starting task execution, serial: {}", serial);
        TaskData<DataType> data = new TaskData<>();
        try {
            if (latch == null || taskDataCarrier == null || serial == null || context == null) {
                log.error("Task initialization error: latch={}, dataCarrier={}, serial={}, context={}", 
                        latch != null, taskDataCarrier != null, serial, context != null);
                throw new RuntimeException("Bad task, can not run it.");
            }
            
            DataType result = execute();
            data.setSuccess(true);
            data.setData(result);
            data.setMessage("ok");
            log.info("Task {} executed successfully", serial);
        } catch (Exception e) {
            log.error("Task {} execution failed: {}", serial, e.getMessage(), e);
            data.setSuccess(false);
            data.setException(e);
            data.setData(null);
            data.setMessage(e.getMessage());
        } finally {
            if (this.taskDataCarrier != null) {
                this.taskDataCarrier.putResult(this.serial, data);
            }
            if (this.latch != null) {
                latch.countDown();
                log.debug("Task {} completed, countdown triggered", serial);
            }
        }
    }

    /**
     * 子类实现具体任务逻辑
     *
     * @return 任务执行结果
     * @throws Exception 执行过程中的异常
     */
    protected abstract DataType execute() throws Exception;

    final void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    final void setSerial(int serial) {
        this.serial = serial;
    }

    final void setDataCarrier(final TaskDataCarrier<DataType> taskDataCarrier) {
        this.taskDataCarrier = taskDataCarrier;
    }
}
