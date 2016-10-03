package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture
import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository
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
@SpringBootTest(classes = DownloadNecessaryPictureServiceTest.class)
class DownloadNecessaryPictureServiceTest {

    def pictureRepository
    def downloadManager

    def downloadNecessaryPictureService

    @Before
    void setUp() {
        pictureRepository = mock(PictureRepository.class)
        downloadManager = mock(PictureDownloadManager.class, (Answer){CompletableFuture.completedFuture(Boolean.TRUE)})

        downloadNecessaryPictureService = new DownloadNecessaryPictureService(pictureRepository, downloadManager)
    }

    @Test
    void testShouldDownloadThumbnailOnJustFoundPicture() {
        // arrange
        def sourcePicture = new InterfaceliftPicture("someId", "someTittle", "test://thumbnailUrl", "test://fullPictureUrl")
        sourcePicture.setStatus(PictureStatus.JUST_FOUND)
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn([sourcePicture])

        // act
        downloadNecessaryPictureService.planNecessaryDownloads()

        // assert
        verify(downloadManager).downloadThumbnail(sourcePicture)
    }

    @Test
    void testShouldDownloadFullPictureOnAcceptedPicture() {
        // arrange
        def sourcePicture = new InterfaceliftPicture("someId", "someTittle", "test://thumbnailUrl", "test://fullPictureUrl")
        sourcePicture.setStatus(PictureStatus.ACCEPTED)
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn([sourcePicture])

        // act
        downloadNecessaryPictureService.planNecessaryDownloads()

        // assert
        verify(downloadManager).downloadFullPicture(sourcePicture)
    }
}
