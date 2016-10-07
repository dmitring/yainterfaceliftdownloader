package com.dmitring.yainterfaceliftdownloader.services.impl;

import com.dmitring.yainterfaceliftdownloader.services.DownloadingPictureTaskManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@Component
public class DownloadingPictureTaskManagerImpl implements DownloadingPictureTaskManager {
    private static final CompletableFuture<Boolean> fakeTaskFuture = CompletableFuture.completedFuture(false);

    private final ConcurrentMap<String, CompletableFuture<Boolean>> pendingDownloadTaskMap;

    public DownloadingPictureTaskManagerImpl() {
        this.pendingDownloadTaskMap = new ConcurrentHashMap<>();
    }

    @Override
    public CompletableFuture<Boolean> run(String taskId, Supplier<Boolean> taskRoutine) {
        CompletableFuture<Boolean> presentTaskFuture = pendingDownloadTaskMap.putIfAbsent(taskId, fakeTaskFuture);
        if (presentTaskFuture != null)
            return null;

        CompletableFuture<Boolean> taskFuture = CompletableFuture.supplyAsync(taskRoutine::get);
        taskFuture.handle((result, exception) -> pendingDownloadTaskMap.remove(taskId));
        pendingDownloadTaskMap.put(taskId, taskFuture);

        return taskFuture;
    }

    @Override
    public void tryCancelTask(String taskId) {
        pendingDownloadTaskMap.computeIfPresent(taskId,
                ((id, taskFuture) -> (taskFuture.cancel(false))? null : taskFuture));
    }

    @Override
    public void stopAllTasks() {
        pendingDownloadTaskMap.forEach((taskKey, task) -> task.cancel(false));
    }
}
