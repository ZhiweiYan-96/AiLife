package com.record.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class TaskExecutor {
    ExecutorService pool = Executors.newFixedThreadPool(5);

    public void excecute(AbsTask<?> task) {
        try {
            this.pool.execute(task);
        } catch (RejectedExecutionException ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.pool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
