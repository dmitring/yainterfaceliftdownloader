package com.dmitring.yainterfaceliftdownloader.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface DownloadingPictureTaskManager {
    CompletableFuture<Boolean> putTask(String taskId, Supplier<Boolean> task);
    void ensureCancelTask(String taskId);
    void stopAllTasks();
}
