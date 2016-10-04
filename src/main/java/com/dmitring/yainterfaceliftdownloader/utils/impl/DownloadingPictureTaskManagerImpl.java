package com.dmitring.yainterfaceliftdownloader.utils.impl;

import com.dmitring.yainterfaceliftdownloader.utils.DownloadingPictureTaskManager;
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

    public CompletableFuture<Boolean> putTask(String taskId, Supplier<Boolean> task) {
        CompletableFuture<Boolean> presentTaskFuture = pendingDownloadTaskMap.putIfAbsent(taskId, fakeTaskFuture);
        if (presentTaskFuture != null)
            return null;

        final CompletableFuture<Boolean> taskFuture = CompletableFuture.supplyAsync(task::get);
        taskFuture.handle((result, exception) -> pendingDownloadTaskMap.remove(taskId));
        pendingDownloadTaskMap.put(taskId, taskFuture);

        return taskFuture;
    }

    public void ensureCancelTask(String taskId) {
        pendingDownloadTaskMap.computeIfPresent(taskId,
                ((id, taskFuture) -> (taskFuture.cancel(false))? null : taskFuture));
    }

    public void stopAllTasks() {
        pendingDownloadTaskMap.forEach((taskKey, task) -> task.cancel(false));
    }
}
