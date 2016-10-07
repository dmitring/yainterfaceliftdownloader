package com.dmitring.yainterfaceliftdownloader.services;

import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo;

/**
 * Handle crawled pictures
 */
public interface CrawledPicturesHandler {
    void handleImage(PictureInfo pictureInfo);
    boolean shouldContinueCrawling();
    void handleCrawlingFinish();
}