package com.lubin.task;

import lombok.Data;

/**
 * 任务执行结果的抽象数据封装类
 * 用于统一处理任务执行的结果、状态和异常信息
 *
 * @param <T> 具体的数据类型
 */
@Data
public class TaskData<T> {
    /**
     * 任务执行是否成功
     */
    private boolean success;
    
    /**
     * 任务执行的结果数据
     */
    private T data;
    
    /**
     * 任务执行的消息（成功或失败的描述）
     */
    private String message;
    
    /**
     * 任务执行过程中的异常信息
     */
    private Exception exception;
}