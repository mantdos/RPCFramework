package com.zxl.core.server.threadpool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;

/**
 * 线程池自定义配置类，可自行根据业务场景修改配置参数。
 */
@Setter
@Getter
public class ThreadPoolConfig {
    /**
     * 线程池默认参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int DMAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private ThreadPoolConfig(){

    }

    // 使用有界队列
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

    public static ExecutorService newThreadPool(){
        return new ThreadPoolExecutor(CORE_POOL_SIZE, DMAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, new ThreadPoolConfig().workQueue, Executors.defaultThreadFactory());
    }
}
