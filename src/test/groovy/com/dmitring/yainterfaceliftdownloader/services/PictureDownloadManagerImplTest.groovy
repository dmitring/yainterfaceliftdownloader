package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.Picture
import com.dmitring.yainterfaceliftdownloader.domain.PictureHandler
import com.dmitring.yainterfaceliftdownloader.domain.TestPictureFactory
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository
import com.dmitring.yainterfaceliftdownloader.services.impl.PictureDownloadManagerImpl
import com.dmitring.yainterfaceliftdownloader.utils.AssertFutureUtil
import com.dmitring.yainterfaceliftdownloader.utils.DownloadingPictureTaskManager
import com.dmitring.yainterfaceliftdownloader.utils.crawler.PictureDownloader
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

import static org.junit.Assert.assertSame
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictureDownloadManagerImplTest.class)
class PictureDownloadManagerImplTest {
    def etalonHashsum = new String("someHashsumInHex")
    def testPictureFactory = new TestPictureFactory();

    def mockPictureHashsumProvider
    def mockPictureHandler
    def mockPictureDownloader
    def mockPictureRepository
    def downloadingPictureTaskManager
    def picture

    def pictureDownloadManager

    static class StubDownloadingPictureTaskManager implements DownloadingPictureTaskManager {
        @Override
        CompletableFuture<Boolean> putTask(String taskId, Supplier<Boolean> task) {
            Boolean result = task.get();
            return CompletableFuture.<Boolean>completedFuture(result);
        }

        @Override
        void ensureCancelTask(String taskId) {

        }

        @Override
        void stopAllTasks() {

        }
    }

    @Before
    void setUp() {
        mockPictureHashsumProvider = mock(PictureHashsumProvider.class)
        when(mockPictureHashsumProvider.getHashsum(any(Picture.class))).thenReturn(etalonHashsum)
        mockPictureHandler = mock(PictureHandler.class)
        mockPictureDownloader = mock(PictureDownloader.class)
        mockPictureRepository = mock(PictureRepository.class)
        downloadingPictureTaskManager = new StubDownloadingPictureTaskManager()
        picture = testPictureFactory.createPicture();

        pictureDownloadManager = new PictureDownloadManagerImpl(mockPictureRepository, mockPictureHashsumProvider,
                mockPictureHandler, mockPictureDownloader, downloadingPictureTaskManager)
    }

    @Test
    void testSuccessfullyDownloadThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadThumbnail(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, true, 100)
        verify(mockPictureHandler).handleThumbnailSuccessDownload(picture)
        verify(mockPictureRepository).save(picture)
        assertSame(etalonHashsum, picture.getThumbnail().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadThumbnail(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, false, 100)
        verify(mockPictureHandler).handleThumbnailFailedDownload(picture)
        verify(mockPictureRepository).save(picture)
    }

    @Test
    void testSuccessfullyDownloadFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadFullPicture(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, true, 100)
        verify(mockPictureHandler).handleFullPictureSuccessDownload(picture)
        verify(mockPictureRepository).save(picture)
        assertSame(etalonHashsum, picture.getFullPicture().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.downloadFullPicture(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, false, 100)
        verify(mockPictureHandler).handleFullPictureFailedDownload(picture)
        verify(mockPictureRepository).save(picture)
    }

    @Test
    void testSuccessfullyRepairThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairThumbnail(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, true, 100)
        verify(mockPictureHandler).handleThumbnailSuccessRepair(picture)
        verify(mockPictureRepository).save(picture)
        assertSame(etalonHashsum, picture.getThumbnail().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadRepairThumbnail() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairThumbnail(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, false, 100)
        verify(mockPictureHandler).handleThumbnailFailedDownload(picture)
        verify(mockPictureRepository).save(picture)
    }

    @Test
    void testSuccessfullyRepairFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(true)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairFullPicture(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, true, 100)
        verify(mockPictureHandler).handleFullPictureSuccessRepair(picture)
        verify(mockPictureRepository).save(picture)
        assertSame(etalonHashsum, picture.getFullPicture().getFileMd5HexHash())
    }

    @Test
    void testFailDownloadRepairFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairFullPicture(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, false, 100)
        verify(mockPictureHandler).handleFullPictureFailedDownload(picture)
        verify(mockPictureRepository).save(picture)
    }

    @Test
    void testEnsureCancelDownloadFullPicture() {
        // arrange
        when(mockPictureDownloader.download(any(String.class), any(String.class))).thenReturn(false)

        // act
        final CompletableFuture<Boolean> downloadResult = pictureDownloadManager.repairFullPicture(picture)

        // assert
        AssertFutureUtil.getAndAssert(downloadResult, false, 100)
        verify(mockPictureHandler).handleFullPictureFailedDownload(picture)
        verify(mockPictureRepository).save(picture)
    }
}
