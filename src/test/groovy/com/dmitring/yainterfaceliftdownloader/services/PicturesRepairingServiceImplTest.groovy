package com.dmitring.yainterfaceliftdownloader.services

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture
import com.dmitring.yainterfaceliftdownloader.domain.Picture
import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus
import com.dmitring.yainterfaceliftdownloader.domain.TestPictureFactory
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository
import com.dmitring.yainterfaceliftdownloader.services.impl.PicturesRepairingServiceImpl
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider
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
@SpringBootTest(classes = PicturesRepairingServiceImplTest.class)
class PicturesRepairingServiceImplTest {

    def etalonHashsum = new String("etalonHashsum")
    def brokenHashsum = new String("brokeHashsum")
    def testPictureFactory = new TestPictureFactory()

    def pictureRepository
    def downloadManager
    def pictureHashsumProvider

    def picturesRepairingService

    @Before
    void setUp() {
        pictureRepository = mock(PictureRepository.class)
        downloadManager = mock(PictureDownloadManager.class, (Answer){CompletableFuture.completedFuture(Boolean.TRUE)})
        pictureHashsumProvider = mock(PictureHashsumProvider.class)
        picturesRepairingService = mock(PictureHashsumProvider.class);
        when(pictureHashsumProvider.getHashsum(any(Picture.class))).thenReturn(etalonHashsum)

        picturesRepairingService = new PicturesRepairingServiceImpl(
                pictureRepository, downloadManager, pictureHashsumProvider)
    }

    def createPicture(def status, def hashsum) {
        def picture = testPictureFactory.createPictureWithStatus(status)
        picture.getThumbnail().setFileMd5HexHash(hashsum)
        picture.getFullPicture().setFileMd5HexHash(hashsum)
        return picture
    }

    def createSafeAndBrokenPicture(def pictureStatus) {
        def safePicture = createPicture(pictureStatus, etalonHashsum)
        def brokenPicture = createPicture(pictureStatus, brokenHashsum)
        return [safePicture, brokenPicture]
    }

    @Test
    void testConsideringPictures() {
        // arrange
        def sourcePictures = createSafeAndBrokenPicture(PictureStatus.CONSIDERING)
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn(sourcePictures)

        // act
        picturesRepairingService.checkPictureHashsums()

        // assert
        verify(pictureHashsumProvider, times(2)).getHashsum(any(Picture.class))
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[0].getThumbnail())
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[1].getThumbnail())

        verify(downloadManager, times(1)).repairThumbnail(any(InterfaceliftPicture.class))
        verify(downloadManager, times(1)).repairThumbnail(sourcePictures[1])

        verify(pictureRepository, times(1)).save(any(InterfaceliftPicture.class))
        verify(pictureRepository, times(1)).save(sourcePictures[1])
    }

    @Test
    void testAcceptedPictures() {
        // arrange
        def sourcePictures = createSafeAndBrokenPicture(PictureStatus.ACCEPTED)
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn(sourcePictures)

        // act
        picturesRepairingService.checkPictureHashsums()

        // assert
        verify(pictureHashsumProvider, times(2)).getHashsum(any(Picture.class))
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[0].getThumbnail())
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[1].getThumbnail())

        verify(downloadManager, times(1)).repairThumbnail(any(InterfaceliftPicture.class))
        verify(downloadManager, times(1)).repairThumbnail(sourcePictures[1])

        verify(pictureRepository, times(1)).save(any(InterfaceliftPicture.class))
        verify(pictureRepository, times(1)).save(sourcePictures[1])
    }

    @Test
    void testRejectedPictures() {
        // arrange
        def sourcePictures = createSafeAndBrokenPicture(PictureStatus.REJECTED)
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn(sourcePictures)

        // act
        picturesRepairingService.checkPictureHashsums()

        // assert
        verify(pictureHashsumProvider, times(2)).getHashsum(any(Picture.class))
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[0].getThumbnail())
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[1].getThumbnail())

        verify(downloadManager, times(1)).repairThumbnail(any(InterfaceliftPicture.class))
        verify(downloadManager, times(1)).repairThumbnail(sourcePictures[1])

        verify(pictureRepository, times(1)).save(any(InterfaceliftPicture.class))
        verify(pictureRepository, times(1)).save(sourcePictures[1])
    }

    @Test
    void testDownloadedPictures() {
        // arrange
        def sourcePictures = createSafeAndBrokenPicture(PictureStatus.DOWNLOADED)
        when(pictureRepository.findByStatusIn(any(Collection.class))).thenReturn(sourcePictures)

        // act
        picturesRepairingService.checkPictureHashsums()

        // assert
        verify(pictureHashsumProvider, times(4)).getHashsum(any(Picture.class))
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[0].getThumbnail())
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[0].getFullPicture())
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[1].getThumbnail())
        verify(pictureHashsumProvider, times(1)).getHashsum(sourcePictures[1].getFullPicture())

        verify(downloadManager, times(1)).repairThumbnail(any(InterfaceliftPicture.class))
        verify(downloadManager, times(1)).repairThumbnail(sourcePictures[1])
        verify(downloadManager, times(1)).repairFullPicture(any(InterfaceliftPicture.class))
        verify(downloadManager, times(1)).repairFullPicture(sourcePictures[1])

        verify(pictureRepository, times(2)).save(any(InterfaceliftPicture.class))
        verify(pictureRepository, times(2)).save(sourcePictures[1])
    }
}
