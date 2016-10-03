package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture
import com.dmitring.yainterfaceliftdownloader.domain.Picture
import com.dmitring.yainterfaceliftdownloader.domain.PictureHandler
import com.dmitring.yainterfaceliftdownloader.utils.DownloadingPictureTaskManager
import com.dmitring.yainterfaceliftdownloader.utils.crawler.PictureDownloader
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import static org.junit.Assert.*
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictureDownloadManagerTest.class)
class PictureDownloadManagerTest {
    def etalonHashsum

    def mockPictureHashsumProvider
    def mockPictureHandler
    def mockPictureDownloader
    def downloadingPictureTaskManager
    def picture

    def pictureDownloadManager

    @Before
    void setUp() {
        etalonHashsum = new String("someHashsumInHex")

        mockPictureHashsumProvider = mock(PictureHashsumProvider.class)
        when(mockPictureHashsumProvider.getHashsum(any(Picture.class))).thenReturn(etalonHashsum)
        mockPictureHandler = mock(PictureHandler.class)
        mockPictureDownloader = mock(PictureDownloader.class)
        downloadingPictureTaskManager = new DownloadingPictureTaskManager()
        picture = new InterfaceliftPicture("pictureId", "pictureTittle", "test://pictureThumbnail", "test://fullPicture")

        pictureDownloadManager = new PictureDownloadManager(
                mockPictureHashsumProvider, mockPictureHandler, mockPictureDownloader, downloadingPictureTaskManager)
    }

    @Test
    void testSuccessfullyDownloadThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadThumbnail(picture)

        // assert
        try {
            assertTrue(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleThumbnailSuccessDownload(any(InterfaceliftPicture.class))
        assertSame(etalonHashsum, picture.getThumbnail().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadThumbnail(picture)

        // assert
        try {
            assertFalse(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleThumbnailFailedDownload(any(InterfaceliftPicture.class))
    }

    @Test
    void testSuccessfullyDownloadFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadFullPicture(picture)

        // assert
        try {
            assertTrue(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleFullPictureSuccessDownload(any(InterfaceliftPicture.class))
        assertSame(etalonHashsum, picture.getFullPicture().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadFullPicture(picture)

        // assert
        try {
            assertFalse(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleFullPictureFailedDownload(any(InterfaceliftPicture.class))
    }

    @Test
    void testSuccessfullyRepairThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairThumbnail(picture)

        // assert
        try {
            assertTrue(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleThumbnailSuccessRepair(any(InterfaceliftPicture.class))
        assertSame(etalonHashsum, picture.getThumbnail().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadRepairThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairThumbnail(picture)

        // assert
        try {
            assertFalse(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleThumbnailFailedDownload(any(InterfaceliftPicture.class))
    }

    @Test
    void testSuccessfullyRepairFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairFullPicture(picture)

        // assert
        try {
            assertTrue(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleFullPictureSuccessRepair(any(InterfaceliftPicture.class))
        assertSame(etalonHashsum, picture.getFullPicture().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadRepairFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairFullPicture(picture)

        // assert
        try {
            assertFalse(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleFullPictureFailedDownload(any(InterfaceliftPicture.class))
    }

    @Test
    void testEnsureCancelDownloadFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairFullPicture(picture)

        // assert
        try {
            assertFalse(downloadResult.get(100, TimeUnit.MILLISECONDS).booleanValue())
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
        verify(mockPictureHandler).handleFullPictureFailedDownload(any(InterfaceliftPicture.class))
    }
}
