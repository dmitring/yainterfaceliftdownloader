package com.dmitring.yainterfaceliftdownloader.services;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.Picture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureHandler;
import com.dmitring.yainterfaceliftdownloader.utils.DownloadingPictureTaskManager;
import com.dmitring.yainterfaceliftdownloader.utils.crawler.PictureDownloader;
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class PictureDownloadManager {
    private static final Logger log = Logger.getLogger(PictureDownloadManager.class.getName());

    private final PictureHashsumProvider pictureHashsumProvider;
    private final PictureHandler pictureHandler;
    private final PictureDownloader pictureDownloader;
    private final DownloadingPictureTaskManager pictureTaskManager;

    @Autowired
    public PictureDownloadManager(PictureHashsumProvider pictureHashsumProvider,
                                  PictureHandler pictureHandler,
                                  PictureDownloader pictureDownloader,
                                  DownloadingPictureTaskManager pictureTaskManager) {
        this.pictureHashsumProvider = pictureHashsumProvider;
        this.pictureHandler = pictureHandler;
        this.pictureDownloader = pictureDownloader;
        this.pictureTaskManager = pictureTaskManager;
    }

    public CompletableFuture<Boolean> downloadThumbnail(InterfaceliftPicture picture) {
        final Picture thumbnail = picture.getThumbnail();

        return download(
                thumbnail.getDownloadUrl(),
                pictureHandler.getThumbnailFilePath(picture),
                () -> {
                    pictureHandler.handleThumbnailSuccessDownload(picture);
                    calculateHashSum(thumbnail);
                },
                () -> pictureHandler.handleThumbnailFailedDownload(picture)
        );
    }

    public CompletableFuture<Boolean> downloadFullPicture(InterfaceliftPicture picture) {
        final Picture fullPicture = picture.getFullPicture();

        return download(
                fullPicture.getDownloadUrl(),
                pictureHandler.getFullPictureFilePath(picture),
                () -> {
                    pictureHandler.handleFullPictureSuccessDownload(picture);
                    calculateHashSum(fullPicture);
                },
                () -> pictureHandler.handleFullPictureFailedDownload(picture)
        );
    }

    public CompletableFuture<Boolean> repairThumbnail(InterfaceliftPicture picture) {
        final Picture thumbnail = picture.getThumbnail();

        return download(
                thumbnail.getDownloadUrl(),
                pictureHandler.getThumbnailFilePath(picture),
                () -> {
                    pictureHandler.handleThumbnailSuccessRepair(picture);
                    calculateHashSum(thumbnail);
                },
                () -> pictureHandler.handleThumbnailFailedDownload(picture)
        );
    }

    public CompletableFuture<Boolean> repairFullPicture(InterfaceliftPicture picture) {
        final Picture fullPicture = picture.getFullPicture();

        return download(
                fullPicture.getDownloadUrl(),
                pictureHandler.getFullPictureFilePath(picture),
                () -> {
                    pictureHandler.handleFullPictureSuccessRepair(picture);
                    calculateHashSum(fullPicture);
                },
                () -> pictureHandler.handleFullPictureFailedDownload(picture)
        );
    }

    private CompletableFuture<Boolean> download(String sourceUrl,
                                             String destinationFilePath,
                                             Runnable onComplete,
                                             Runnable onFail) {
        CompletableFuture<Boolean> downloadingTask = pictureTaskManager.putTask(
                sourceUrl,
                () -> downloadRoutine(sourceUrl, destinationFilePath)
        );
        if (downloadingTask != null) {
            downloadingTask = downloadingTask.thenApply(result -> {
                if (result) {
                    onComplete.run();
                } else {
                    onFail.run();
                }
                return result;
            });
        }

        return downloadingTask;
    }

    public void ensureCancelDownloadFullPicture(InterfaceliftPicture canceling) {
        pictureTaskManager.ensureCancelTask(canceling.getFullPicture().getDownloadUrl());
    }

    @PreDestroy
    public void stop() {
        log.info("MultiplePicturesDownloadManagerService is stopping");
        pictureTaskManager.stopAllTasks();
    }

    private void calculateHashSum(Picture certainPicture) {
        String hashSum = pictureHashsumProvider.getHashsum(certainPicture);
        certainPicture.setFileMd5HexHash(hashSum);
    }

    private boolean downloadRoutine(String sourceUrl, String destinationFilePath) {
        return pictureDownloader.download(sourceUrl, destinationFilePath);
    }
}
