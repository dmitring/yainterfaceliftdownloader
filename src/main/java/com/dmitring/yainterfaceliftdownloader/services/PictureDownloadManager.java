package com.dmitring.yainterfaceliftdownloader.services;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;

import java.util.concurrent.CompletableFuture;

public interface PictureDownloadManager {
    CompletableFuture<Boolean> downloadThumbnail(InterfaceliftPicture picture);
    CompletableFuture<Boolean> downloadFullPicture(InterfaceliftPicture picture);
    CompletableFuture<Boolean> repairThumbnail(InterfaceliftPicture picture);
    CompletableFuture<Boolean> repairFullPicture(InterfaceliftPicture picture);
    void ensureCancelDownloadFullPicture(InterfaceliftPicture canceling);
    void stop();
}
