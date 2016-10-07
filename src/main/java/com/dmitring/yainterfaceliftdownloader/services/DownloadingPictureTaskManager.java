package com.dmitring.yainterfaceliftdownloader.services;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Maintain and control download tasks
 */
public interface DownloadingPictureTaskManager {
    /**
     * @param taskId is an unique task identifier
     * @param taskRoutine is a task function that returns Boolean (success or not)
     * @return a CompletableFuture of task if the task has got to execute or null if the task has rejected because
     *  taskId is in TaskManager queue now
     */
    CompletableFuture<Boolean> run(String taskId, Supplier<Boolean> taskRoutine);
    void tryCancelTask(String taskId);
    void stopAllTasks();
}
