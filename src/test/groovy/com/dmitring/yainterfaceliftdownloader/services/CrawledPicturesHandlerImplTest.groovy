package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.ApplicationVariable
import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture
import com.dmitring.yainterfaceliftdownloader.domain.PictureHandler
import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo
import com.dmitring.yainterfaceliftdownloader.repositories.ApplicationVariableRepository
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository
import com.dmitring.yainterfaceliftdownloader.services.impl.CrawledPicturesHandlerImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.*
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = "com.dmitring.yainterfaceliftdownloader.successCountInARow = 10",
        classes = CrawledPicturesHandlerImplTest.class)
class CrawledPicturesHandlerImplTest {

    def maxSuccessCountInARow
    def rawCrawlFinishedFalse
    def rawCrawlFinishedTrue

    def mockPicture

    def knownMockPictureInfo
    def unknownMockPictureInfo

    def pictureRepository
    def applicationVariablesRepository
    def downloadManager
    def pictureHandler

    def newPictureFoundHandler

    @Before
    void setUp() {
        maxSuccessCountInARow = 10
        rawCrawlFinishedFalse = new ApplicationVariable("CRAWLING_FINISHED", "false")
        rawCrawlFinishedTrue = new ApplicationVariable("CRAWLING_FINISHED", "true")

        mockPicture = mock(InterfaceliftPicture.class)
        knownMockPictureInfo = mock(PictureInfo.class)
        when(knownMockPictureInfo.getId()).thenReturn("knownPicture")
        unknownMockPictureInfo = mock(PictureInfo.class)
        when(unknownMockPictureInfo.getId()).thenReturn("unknownPicture")

        pictureRepository = mock(PictureRepository.class)
        applicationVariablesRepository = mock(ApplicationVariableRepository.class)
        downloadManager = mock(PictureDownloadManager.class)
        pictureHandler = mock(PictureHandler.class)

        newPictureFoundHandler = new CrawledPicturesHandlerImpl(pictureRepository, applicationVariablesRepository,
                downloadManager, pictureHandler, maxSuccessCountInARow)
    }

    @Test
    void testInitShouldCreateVariableIfNotExists() {
        // arrange
        when(applicationVariablesRepository.findOne("CRAWLING_FINISHED")).thenReturn(null)

        // act
        newPictureFoundHandler.init()

        // assert
        ArgumentCaptor<ApplicationVariable> appVarsCaptor = ArgumentCaptor.forClass(ApplicationVariable.class);
        verify(applicationVariablesRepository).save(appVarsCaptor.capture())
        def testAppVar = appVarsCaptor.getValue()
        assertEquals(rawCrawlFinishedFalse, testAppVar)
    }

    @Test
    void testContinueToEndIfNotFinished() {
        // arrange
        when(applicationVariablesRepository.findOne("CRAWLING_FINISHED")).thenReturn(rawCrawlFinishedFalse)
        when(pictureRepository.findOne(any(String.class))).thenReturn(mockPicture)

        // act
        newPictureFoundHandler.init()
        def shouldContinueZeroHandled = newPictureFoundHandler.shouldContinueCrawling()
        newPictureFoundHandler.handleImage(knownMockPictureInfo)
        def shouldContinueOneHandled = newPictureFoundHandler.shouldContinueCrawling()
        for (def i in 2..maxSuccessCountInARow*2)
            newPictureFoundHandler.handleImage(knownMockPictureInfo)
        def shouldContinueEnoughHandled = newPictureFoundHandler.shouldContinueCrawling()

        // assert
        assertTrue(shouldContinueZeroHandled)
        assertTrue(shouldContinueOneHandled)
        assertTrue(shouldContinueEnoughHandled)
    }

    @Test
    void testStopIfOnceFinished() {
        // arrange
        when(applicationVariablesRepository.findOne("CRAWLING_FINISHED")).thenReturn(rawCrawlFinishedTrue)
        when(pictureRepository.findOne(any(String.class))).thenReturn(mockPicture)

        // act
        newPictureFoundHandler.init()
        def shouldContinueZeroHandled = newPictureFoundHandler.shouldContinueCrawling()
        newPictureFoundHandler.handleImage(knownMockPictureInfo)
        def shouldContinueOneHandled = newPictureFoundHandler.shouldContinueCrawling()
        for (def i in 2..maxSuccessCountInARow)
            newPictureFoundHandler.handleImage(knownMockPictureInfo)
        def shouldContinueEnoughHandled = newPictureFoundHandler.shouldContinueCrawling()

        // assert
        assertTrue(shouldContinueZeroHandled)
        assertTrue(shouldContinueOneHandled)
        assertFalse(shouldContinueEnoughHandled)
    }

    @Test
    void testContinueIfUnknownPictureInTheMiddle() {
        // arrange
        when(applicationVariablesRepository.findOne("CRAWLING_FINISHED")).thenReturn(rawCrawlFinishedTrue)
        when(pictureRepository.findOne(knownMockPictureInfo.getId())).thenReturn(mockPicture)
        when(pictureRepository.findOne(unknownMockPictureInfo.getId())).thenReturn(null)

        // act
        newPictureFoundHandler.init()
        for (def i in 1..maxSuccessCountInARow/2)
            newPictureFoundHandler.handleImage(knownMockPictureInfo)
        newPictureFoundHandler.handleImage(unknownMockPictureInfo)
        for (def i in maxSuccessCountInARow/2..maxSuccessCountInARow)
            newPictureFoundHandler.handleImage(knownMockPictureInfo)
        def shouldContinueEnoughHandled = newPictureFoundHandler.shouldContinueCrawling()

        // assert
        assertTrue(shouldContinueEnoughHandled)
    }

    @Test
    void testStopAfterEnoughKnownAfterUnknown() {
        // arrange
        when(applicationVariablesRepository.findOne("CRAWLING_FINISHED")).thenReturn(rawCrawlFinishedTrue)
        when(pictureRepository.findOne(knownMockPictureInfo.getId())).thenReturn(mockPicture)
        when(pictureRepository.findOne(unknownMockPictureInfo.getId())).thenReturn(null)

        // act
        newPictureFoundHandler.init()
        for (def i in 1..maxSuccessCountInARow/2)
            newPictureFoundHandler.handleImage(knownMockPictureInfo)
        newPictureFoundHandler.handleImage(unknownMockPictureInfo)
        for (def i in 1..maxSuccessCountInARow)
            newPictureFoundHandler.handleImage(knownMockPictureInfo)
        def shouldContinueEnoughHandled = newPictureFoundHandler.shouldContinueCrawling()

        // assert
        assertFalse(shouldContinueEnoughHandled)
    }

    @Test
    void testHandleFinished() {
        // arrange
        when(applicationVariablesRepository.findOne("CRAWLING_FINISHED")).thenReturn(rawCrawlFinishedFalse)

        // act
        newPictureFoundHandler.init()
        newPictureFoundHandler.handleCrawlingFinish()

        // assert
        ArgumentCaptor<ApplicationVariable> appVarsCaptor = ArgumentCaptor.forClass(ApplicationVariable.class);
        verify(applicationVariablesRepository).save(appVarsCaptor.capture())
        def testAppVar = appVarsCaptor.getValue()
        assertEquals(rawCrawlFinishedTrue, testAppVar)
    }

}