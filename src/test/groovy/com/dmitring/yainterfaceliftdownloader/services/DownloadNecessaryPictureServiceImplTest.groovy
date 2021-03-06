package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus
import com.dmitring.yainterfaceliftdownloader.domain.TestPictureFactory
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository
import com.dmitring.yainterfaceliftdownloader.services.impl.DownloadNecessaryPictureServiceImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.stubbing.Answer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.CompletableFuture

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DownloadNecessaryPictureServiceImplTest.class)
class DownloadNecessaryPictureServiceImplTest {

    def testPictureFactory = new TestPictureFactory()

    def pictureRepository
    def downloadManager
    def downloadNecessaryPictureService

    @Before
    void setUp() {
        pictureRepository = mock(PictureRepository.class)
        downloadManager = mock(PictureDownloadManager.class, (Answer){CompletableFuture.completedFuture(Boolean.TRUE)})

        downloadNecessaryPictureService = new DownloadNecessaryPictureServiceImpl(pictureRepository, downloadManager)
    }

    @Test
    void testShouldDownloadThumbnailOnJustFoundPicture() {
        // arrange
        def sourcePicture = testPictureFactory.createPictureWithStatus(PictureStatus.JUST_FOUND);
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn([sourcePicture])

        // act
        downloadNecessaryPictureService.planNecessaryDownloads()

        // assert
        verify(downloadManager).downloadThumbnail(sourcePicture)
    }

    @Test
    void testShouldDownloadFullPictureOnAcceptedPicture() {
        // arrange
        def sourcePicture = testPictureFactory.createPictureWithStatus(PictureStatus.ACCEPTED);
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn([sourcePicture])

        // act
        downloadNecessaryPictureService.planNecessaryDownloads()

        // assert
        verify(downloadManager).downloadFullPicture(sourcePicture)
    }
}
