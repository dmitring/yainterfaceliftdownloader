package com.dmitring.yainterfaceliftdownloader.services.impl;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus;
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository;
import com.dmitring.yainterfaceliftdownloader.services.DownloadNecessaryPictureService;
import com.dmitring.yainterfaceliftdownloader.services.PictureDownloadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class DownloadNecessaryPictureServiceImpl implements DownloadNecessaryPictureService {
    private static final Logger log = Logger.getLogger(DownloadNecessaryPictureServiceImpl.class.getName());

    private final PictureRepository pictureRepository;
    private final PictureDownloadManager downloadManager;

    @Autowired
    public DownloadNecessaryPictureServiceImpl(PictureRepository pictureRepository, PictureDownloadManager downloadManager) {
        this.pictureRepository = pictureRepository;
        this.downloadManager = downloadManager;
    }

    @Scheduled(fixedDelayString = "${com.dmitring.yainterfaceliftdownloader.downloadNecessaryDelay}")
    @Transactional
    @Override
    public void planNecessaryDownloads() {
        log.info("The downloading necessary pictures process has started");
        pictureRepository.findByStatusIn(Arrays.asList(
                PictureStatus.JUST_FOUND,
                PictureStatus.ACCEPTED))
            .forEach(this::handleSinglePicture);
        log.info("The downloading necessary pictures process has stopped");
    }

    private void handleSinglePicture(InterfaceliftPicture picture) {
        if (picture.getStatus() == PictureStatus.JUST_FOUND)
            downloadManager.downloadThumbnail(picture);
        else
            downloadManager.downloadFullPicture(picture);
    }
}
