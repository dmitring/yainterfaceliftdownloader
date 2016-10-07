package com.dmitring.yainterfaceliftdownloader.services.impl;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;
import com.dmitring.yainterfaceliftdownloader.services.CrawlerService;
import com.dmitring.yainterfaceliftdownloader.services.CrawledPicturesHandler;
import com.dmitring.yainterfaceliftdownloader.services.crawler.PageCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    private static final Logger log = Logger.getLogger(CrawlerServiceImpl.class.getName());

    private final CrawledPicturesHandler newPictureFoundHandler;
    private final PageCrawler pageCrawler;
    private final int maxAttemptCount;

    private final AtomicBoolean isRunning;

    @Autowired
    public CrawlerServiceImpl(CrawledPicturesHandler newPictureFoundHandler,
                              PageCrawler pageCrawler,
                              @Value("${com.dmitring.yainterfaceliftdownloader.maxCrawlerAttempts}") int maxAttemptCount) {
        this.newPictureFoundHandler = newPictureFoundHandler;
        this.pageCrawler = pageCrawler;
        this.maxAttemptCount = maxAttemptCount;

        this.isRunning = new AtomicBoolean(false);
    }

    @Scheduled(fixedDelayString = "${com.dmitring.yainterfaceliftdownloader.crawlingDelay}")
    @Override
    public boolean startCrawling() {
        log.info("A next crawling start is planning");
        if (isRunning.getAndSet(true)) {
            log.warning("The crawling can't start due to another crawling process is running");
            return false;
        }

        log.info("The crawling has started");
        crawl();
        return true;
    }

    private void crawl() {
        int pageNumber = 1;
        while (isRunning.get() && handleSinglePage(pageNumber)) {
            pageNumber++;
        }
        newPictureFoundHandler.handleCrawlingFinish();
        isRunning.compareAndSet(true, false);
    }

    private boolean handleSinglePage(int pageNumber) {
        ParsedPage parsedPage;
        boolean parsedUnsuccessfully;
        int attemptCount = 0;
        do {
            parsedPage = pageCrawler.getPagePictureInfo(pageNumber);
            parsedUnsuccessfully = (parsedPage == null);
            attemptCount++;
        } while (parsedUnsuccessfully && attemptCount < maxAttemptCount);

        if (!parsedUnsuccessfully)
            parsedPage.getPictureInfo().forEach(newPictureFoundHandler::handleImage);

        return (!parsedUnsuccessfully && newPictureFoundHandler.shouldContinueCrawling() && parsedPage.isNextPageExists());
    }
}
