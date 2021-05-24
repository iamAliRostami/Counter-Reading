package com.leon.counter_reading.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTaskRunner<T> {

    private ExecutorService executorService;
    private final Set<Callable<T>> tasks = new HashSet<>();
    public AsyncTaskRunner() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public AsyncTaskRunner(int threadNum) {
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }


    public void addTask(Callable<T> task) {
        tasks.add(task);
    }

    public void execute() {
        try {
            this.onPostExecute();
        } finally {
            executorService.shutdown();
        }

    }

    protected abstract void onPostExecute();

    protected void onCancelled() {
    }

}
