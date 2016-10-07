package com.dmitring.yainterfaceliftdownloader.services;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;

import java.util.concurrent.CompletableFuture;

/**
 * Provides functions to asynchronously download and repair pictures. Also provides routines to cancel tasks.
 */
public interface PictureDownloadManager {
    CompletableFuture<Boolean> downloadThumbnail(InterfaceliftPicture picture);
    CompletableFuture<Boolean> downloadFullPicture(InterfaceliftPicture picture);
    CompletableFuture<Boolean> repairThumbnail(InterfaceliftPicture picture);
    CompletableFuture<Boolean> repairFullPicture(InterfaceliftPicture picture);
    void ensureCancelDownloadFullPicture(InterfaceliftPicture canceling);
    void stop();
}
