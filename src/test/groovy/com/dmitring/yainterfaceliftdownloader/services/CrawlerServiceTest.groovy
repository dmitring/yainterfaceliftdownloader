package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage
import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo
import com.dmitring.yainterfaceliftdownloader.utils.crawler.PageCrawler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DownloadNecessaryPictureServiceTest.class)
class CrawlerServiceTest {

    def pictureFoundHandler
    def pageCrawler
    def maxAttemptCount

    def crawlerService

    @Before
    void setUp() {
        pictureFoundHandler = mock(NewPictureFoundHandlerService.class)
        pageCrawler = mock(PageCrawler.class)
        maxAttemptCount = 1
        crawlerService = new CrawlerService(pictureFoundHandler, pageCrawler, maxAttemptCount)
    }

    @Test
    void testImpossibleSimultaneouslyCrawling() {
        // act
        def hasCrawlingStartedFirstTimeFuture = CompletableFuture.supplyAsync(crawlerService.&startCrawling)
        def hasCrawlingStartedSecondTimeFuture = CompletableFuture.supplyAsync(crawlerService.&startCrawling)
        def hasCrawlingStartedFirstTime = hasCrawlingStartedFirstTimeFuture.get(1000, TimeUnit.MILLISECONDS)
        def hasCrawlingStartedSecondTime = hasCrawlingStartedSecondTimeFuture.get(1000, TimeUnit.MILLISECONDS)

        // assert
        assertTrue(hasCrawlingStartedFirstTime != hasCrawlingStartedSecondTime)
    }

    @Test
    void testPossibleSequentlyCrawling() {
        // act
        def hasCrawlingStartedFirstTimeFuture = CompletableFuture.supplyAsync(crawlerService.&startCrawling)
        def hasCrawlingStartedFirstTime = hasCrawlingStartedFirstTimeFuture.get(1000, TimeUnit.MILLISECONDS)
        def hasCrawlingStartedSecondTimeFuture = CompletableFuture.supplyAsync(crawlerService.&startCrawling)
        def hasCrawlingStartedSecondTime = hasCrawlingStartedSecondTimeFuture.get(1000, TimeUnit.MILLISECONDS)

        // assert
        assertTrue(hasCrawlingStartedFirstTime)
        assertTrue(hasCrawlingStartedSecondTime)
    }

    @Test
    void testEachPictureHandled() {
        // arrange
        def pictures = [
                new PictureInfo("1", "p1", "test://t1", "test://f1"),
                new PictureInfo("2", "p2", "test://t2", "test://f2"),
                new PictureInfo("3", "p3", "test://t3", "test://f3")
        ]
        when(pageCrawler.getPagePictureInfo(any(int))).thenReturn(new ParsedPage(pictures.toList(), false))
        maxAttemptCount = 1
        crawlerService = new CrawlerService(pictureFoundHandler, pageCrawler, maxAttemptCount)

        // act
        crawlerService.startCrawling()

        // assert
        verify(pictureFoundHandler, times(pictures.size())).handleImage(any(PictureInfo.class))
        for (def picture : pictures)
            verify(pictureFoundHandler).handleImage(picture)
    }
}
