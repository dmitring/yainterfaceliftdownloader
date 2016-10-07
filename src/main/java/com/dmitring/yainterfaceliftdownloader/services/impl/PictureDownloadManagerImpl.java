package com.dmitring.yainterfaceliftdownloader.services.impl;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.Picture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureHandler;
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository;
import com.dmitring.yainterfaceliftdownloader.services.PictureDownloadManager;
import com.dmitring.yainterfaceliftdownloader.services.DownloadingPictureTaskManager;
import com.dmitring.yainterfaceliftdownloader.services.crawler.PictureDownloader;
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class PictureDownloadManagerImpl implements PictureDownloadManager {
    private static final Logger log = Logger.getLogger(PictureDownloadManagerImpl.class.getName());

    private final PictureRepository pictureRepository;
    private final PictureHashsumProvider pictureHashsumProvider;
    private final PictureHandler pictureHandler;
    private final PictureDownloader pictureDownloader;
    private final DownloadingPictureTaskManager pictureTaskManager;

    @Autowired
    public PictureDownloadManagerImpl(PictureRepository pictureRepository,
                                      PictureHashsumProvider pictureHashsumProvider,
                                      PictureHandler pictureHandler,
                                      PictureDownloader pictureDownloader,
                                      DownloadingPictureTaskManager pictureTaskManager) {
        this.pictureRepository = pictureRepository;
        this.pictureHashsumProvider = pictureHashsumProvider;
        this.pictureHandler = pictureHandler;
        this.pictureDownloader = pictureDownloader;
        this.pictureTaskManager = pictureTaskManager;
    }

    @Override
    public CompletableFuture<Boolean> downloadThumbnail(InterfaceliftPicture picture) {
        final Picture thumbnail = picture.getThumbnail();

        return download(
                picture,
                thumbnail,
                thumbnail.getDownloadUrl(),
                pictureHandler.getThumbnailFilePath(picture),
                () -> {
                    pictureHandler.handleThumbnailSuccessDownload(picture);
                    calculateHashSum(thumbnail);
                },
                () -> pictureHandler.handleThumbnailFailedDownload(picture)
        );
    }

    @Override
    public CompletableFuture<Boolean> downloadFullPicture(InterfaceliftPicture picture) {
        final Picture fullPicture = picture.getFullPicture();

        return download(
                picture,
                fullPicture,
                fullPicture.getDownloadUrl(),
                pictureHandler.getFullPictureFilePath(picture),
                () -> {
                    pictureHandler.handleFullPictureSuccessDownload(picture);
                    calculateHashSum(fullPicture);
                },
                () -> pictureHandler.handleFullPictureFailedDownload(picture)
        );
    }

    @Override
    public CompletableFuture<Boolean> repairThumbnail(InterfaceliftPicture picture) {
        final Picture thumbnail = picture.getThumbnail();

        return download(
                picture,
                thumbnail,
                thumbnail.getDownloadUrl(),
                pictureHandler.getThumbnailFilePath(picture),
                () -> {
                    pictureHandler.handleThumbnailSuccessRepair(picture);
                    calculateHashSum(thumbnail);
                },
                () -> pictureHandler.handleThumbnailFailedDownload(picture)
        );
    }

    @Override
    public CompletableFuture<Boolean> repairFullPicture(InterfaceliftPicture picture) {
        final Picture fullPicture = picture.getFullPicture();

        return download(
                picture,
                fullPicture,
                fullPicture.getDownloadUrl(),
                pictureHandler.getFullPictureFilePath(picture),
                () -> {
                    pictureHandler.handleFullPictureSuccessRepair(picture);
                    calculateHashSum(fullPicture);
                },
                () -> pictureHandler.handleFullPictureFailedDownload(picture)
        );
    }

    private CompletableFuture<Boolean> download(InterfaceliftPicture picture,
                                                Picture certainPicture,
                                                String sourceUrl,
                                                String destinationFilePath,
                                                Runnable onComplete,
                                                Runnable onFail) {
        CompletableFuture<Boolean> downloadingTask = pictureTaskManager.run(
                sourceUrl,
                () -> downloadRoutine(sourceUrl, destinationFilePath)
        );
        if (downloadingTask != null) {
            downloadingTask = downloadingTask.thenApply(result -> {
                if (result) {
                    onComplete.run();
                    calculateHashSum(certainPicture);
                } else {
                    onFail.run();
                }
                pictureRepository.save(picture);
                return result;
            });
        }

        return downloadingTask;
    }

    @Override
    public void ensureCancelDownloadFullPicture(InterfaceliftPicture canceling) {
        pictureTaskManager.tryCancelTask(canceling.getFullPicture().getDownloadUrl());
    }

    @PreDestroy
    @Override
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
