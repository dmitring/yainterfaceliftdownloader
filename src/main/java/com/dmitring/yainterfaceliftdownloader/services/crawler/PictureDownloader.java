package com.dmitring.yainterfaceliftdownloader.services.crawler;

/**
 * Download concrete picture from source url and save it to destination local (relative or absolute) path
 */
public interface PictureDownloader {
    boolean download(String sourceUrl, String destinationFilePath);
}
