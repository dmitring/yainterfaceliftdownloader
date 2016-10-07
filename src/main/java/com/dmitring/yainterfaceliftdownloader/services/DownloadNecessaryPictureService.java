package com.dmitring.yainterfaceliftdownloader.services;

/**
 * Will download pictures if they are in special states: JUST_FOUND -> CONSIDERING and ACCEPTED -> DOWNLOADED
 */
public interface DownloadNecessaryPictureService {
    void planNecessaryDownloads();
}
