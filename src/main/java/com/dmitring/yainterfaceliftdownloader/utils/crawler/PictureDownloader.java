package com.dmitring.yainterfaceliftdownloader.utils.crawler;

public interface PictureDownloader {
    boolean download(String sourceUrl, String destinationFilePath);
}
