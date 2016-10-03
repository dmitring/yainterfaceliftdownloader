package com.dmitring.yainterfaceliftdownloader.services;

import com.dmitring.yainterfaceliftdownloader.domain.ApplicationVariable;
import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.repositories.ApplicationVariableRepository;
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository;
import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Service
public class NewPictureFoundHandlerService {
    private static final Logger log = Logger.getLogger(NewPictureFoundHandlerService.class.getName());
    private static final String CRAWLING_FINISHED_KEY = "CRAWLING_FINISHED";

    private final PictureRepository pictureRepository;
    private final ApplicationVariableRepository applicationVariables;
    private final PictureDownloadManager downloadManager;
    private final int maxSuccessCountInARow;

    private AtomicBoolean crawlingFinished;
    private AtomicInteger successCountInARow;

    @Autowired
    public NewPictureFoundHandlerService(PictureRepository pictureRepository,
                                         ApplicationVariableRepository applicationVariables,
                                         PictureDownloadManager downloadManager,
                                         @Value("${com.dmitring.yainterfaceliftdownloader.successCountInARow}") int maxSuccessCountInARow) {
        this.pictureRepository = pictureRepository;
        this.applicationVariables = applicationVariables;
        this.downloadManager = downloadManager;
        this.maxSuccessCountInARow = maxSuccessCountInARow;

        crawlingFinished = new AtomicBoolean(Boolean.FALSE);
        successCountInARow = new AtomicInteger(0);
    }

    @PostConstruct
    @Transactional
    public void init() {
        ApplicationVariable rawCrawlingFinished = applicationVariables.findOne(CRAWLING_FINISHED_KEY);;

        if (rawCrawlingFinished == null) {
            rawCrawlingFinished = new ApplicationVariable(CRAWLING_FINISHED_KEY, Boolean.toString(false));
            applicationVariables.save(rawCrawlingFinished);
        }
        else {
            crawlingFinished.set(Boolean.parseBoolean(rawCrawlingFinished.getVariableValue()));
        }
    }

    public void handleImage(PictureInfo pictureInfo) {
        InterfaceliftPicture picture = pictureRepository.findOne(pictureInfo.getId());
        if (picture != null) {
            log.fine(String.format("The picture has already known %s: ", picture.toString()));
            successCountInARow.incrementAndGet();
            return;
        }

        successCountInARow.set(0);
        picture = new InterfaceliftPicture(pictureInfo.getId(), pictureInfo.getTitle(), pictureInfo.getThumbnailUrlString(), pictureInfo.getFullUrlString());
        pictureRepository.save(picture);
        downloadManager.downloadThumbnail(picture);
    }

    public boolean shouldContinue() {
        boolean result = true;
        if (crawlingFinished.get()) {
            result = (successCountInARow.get() < maxSuccessCountInARow);
        }

        return result;
    }

    @Transactional
    public void handleFinish() {
        log.fine("The crawling process has been successfully finished");
        crawlingFinished.set(true);
        final ApplicationVariable rawCrawlingFinished = new ApplicationVariable(CRAWLING_FINISHED_KEY, Boolean.toString(true));
        applicationVariables.save(rawCrawlingFinished);
    }
}
