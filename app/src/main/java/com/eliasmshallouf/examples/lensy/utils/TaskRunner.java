package com.eliasmshallouf.examples.lensy.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskRunner {
    @FunctionalInterface public interface Callback<R> {
        void onComplete(R result);
    }

    private static TaskRunner instance;

    public static TaskRunner getInstance() {
        if(instance == null)
            instance = new TaskRunner();
        return instance;
    }

    private final Executor executor =
            new ThreadPoolExecutor(5, 128, 1,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private final Handler handler = new Handler(Looper.getMainLooper());

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback, Callback<String> errorCallback) {
        executor.execute(() -> {
            final R result;
            try {
                result = callable.call();
                if(callback != null) {
                    handler.post(() -> {
                        callback.onComplete(result);
                    });
                }
            } catch (Exception e) {
                if(errorCallback != null)
                    errorCallback.onComplete(e.toString());
            }
        });
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback){
        executeAsync(callable, callback, null);
    }

    public <R> void executeAsync(Callable<R> callable){
        executeAsync(callable, null, null);
    }
}