package com.dmitring.yainterfaceliftdownloader.domain

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.spy

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictureHandlerTest.class)
class PictureHandlerTest {
    class PictureState {
        def status;
        def thumbnailIsBroken;
        def fullPictureIsBroken;
    }

    def pictureHandler;
    def spyErrorfulChecker;
    def manyTimes = 1000;

    @Before
    void setUp() {
        spyErrorfulChecker = spy(PictureErrorfulChecker.class);
        pictureHandler = new PictureHandler(spyErrorfulChecker);
    }

    def createPicture() {
        return new InterfaceliftPicture("safePicturesId", "safePictures", "test://thumbnail", "test://fullPicture");
    }

    def createPictureWithBrokenState(def thumbnailBroken, def fullPictureBroken) {
        def picture = createPicture();
        if (thumbnailBroken)
            picture.getThumbnail().setBroken(true);
        if (fullPictureBroken)
            picture.getFullPicture().setBroken(true);
        return picture;
    }

    def getAllBrokenPermutationsMap() {
        def brokenPermutations = [
                [false, false],
                [true, false],
                [false, true],
                [true, true],
        ]
        def brokenPictureMap = brokenPermutations.collectEntries({
            brokenState -> [brokenState, createPictureWithBrokenState(brokenState)]});

        return brokenPictureMap;
    }

    def createPictureWithStatus(def status) {
        def picture = createPicture();
        picture.setStatus(status);
        return picture;
    }

    def getAllStatusesAndBrokenPermutations() {
        def allStatusesAndBrokenPermutations = [:]
        def pictureStatuses = PictureStatus.values()

        pictureStatuses.each {status ->
            getAllBrokenPermutationsMap().each {brokenState, picture ->
                picture.setStatus status
                allStatusesAndBrokenPermutations.put(
                        new PictureState(
                                status: status,
                                thumbnailIsBroken: brokenState[0],
                                fullPictureIsBroken: brokenState[1]
                        ),
                        picture)
            }
        }

        return allStatusesAndBrokenPermutations;
    }

    def testPicture(picture, pictureConsumer) {
        try {
            pictureConsumer(picture);
            return picture;
        } catch (Exception exception) {
            return exception;
        }
    }

    @Test
    void testHandleThumbnailSuccessDownload() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleThumbnailSuccessDownload(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.JUST_FOUND) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertTrue("Status must become CONSIDERING", result.getStatus() == PictureStatus.CONSIDERING);
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    void testHandleThumbnailSuccessRepair() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleThumbnailSuccessRepair(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getThumbnailIsBroken()) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertFalse("Thumbnail must become safe", result.getThumbnail().isBroken());
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    void testHandleThumbnailFailedDownload() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations()
        def onceFailedPicture = createPictureWithStatus(PictureStatus.JUST_FOUND)
        def manyManyTimesFailedPicture = createPictureWithStatus(PictureStatus.JUST_FOUND)
        def manyManyTimesFailedPictureBeforeFailed

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleThumbnailFailedDownload(pic)}
                )]
        }

        onceFailedPicture = testPicture(onceFailedPicture, {picture ->
            pictureHandler.handleThumbnailFailedDownload(picture)})

        for (def i in 1..manyTimes) {
            manyManyTimesFailedPictureBeforeFailed = manyManyTimesFailedPicture
            manyManyTimesFailedPicture = testPicture(onceFailedPicture, {picture ->
                pictureHandler.handleThumbnailFailedDownload(manyManyTimesFailedPicture)})
            if (manyManyTimesFailedPicture instanceof IllegalArgumentException)
                break;
        }


        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.JUST_FOUND || pictureState.getThumbnailIsBroken()) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture)
                assertTrue("Picture must has errorCount increased", result.getErrorCount() == 1)
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException)
            }
        }
        assertFalse("Once failed must not become errorful", onceFailedPicture.getStatus() != PictureStatus.ERRORFUL)
        assertTrue("Many times failed must become errorful",
                manyManyTimesFailedPictureBeforeFailed.getStatus() == PictureStatus.ERRORFUL)
        assertTrue("There must be an exception", manyManyTimesFailedPicture instanceof IllegalArgumentException)
    }

    @Test
    void testHandleFullPictureSuccessDownload() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleFullPictureSuccessDownload(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.ACCEPTED) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertTrue("Status must become CONSIDERING", result.getStatus() == PictureStatus.DOWNLOADED);
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    void testHandleFullPictureSuccessRepair() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleFullPictureSuccessRepair(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getFullPictureIsBroken()) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertFalse("Thumbnail must become safe", result.getFullPicture().isBroken());
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    void testHandleFullPictureFailedDownload() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations()
        def onceFailedPicture = createPictureWithStatus(PictureStatus.ACCEPTED)
        def manyManyTimesFailedPicture = createPictureWithStatus(PictureStatus.ACCEPTED)
        def manyManyTimesFailedPictureBeforeFailed

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleFullPictureFailedDownload(pic)}
                )]
        }

        onceFailedPicture = testPicture(onceFailedPicture, {picture ->
            pictureHandler.handleFullPictureFailedDownload(picture)})

        for (def i in 1..manyTimes) {
            manyManyTimesFailedPictureBeforeFailed = manyManyTimesFailedPicture
            manyManyTimesFailedPicture = testPicture(onceFailedPicture, {picture ->
                pictureHandler.handleFullPictureFailedDownload(manyManyTimesFailedPicture)})
            if (manyManyTimesFailedPicture instanceof IllegalArgumentException)
                break;
        }


        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.ACCEPTED || pictureState.getFullPictureIsBroken()) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture)
                assertTrue("Picture must has errorCount increased", result.getErrorCount() == 1)
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException)
            }
        }
        assertFalse("Once failed must not become errorful", onceFailedPicture.getStatus() != PictureStatus.ERRORFUL)
        assertTrue("Many times failed must become errorful",
                manyManyTimesFailedPictureBeforeFailed.getStatus() == PictureStatus.ERRORFUL)
        assertTrue("There must be an exception", manyManyTimesFailedPicture instanceof IllegalArgumentException)
    }

    @Test
    void testHandleAccept() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleAccept(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.CONSIDERING || pictureState.getStatus() == PictureStatus.REJECTED) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertTrue("Thumbnail must become safe", result.getStatus() == PictureStatus.ACCEPTED);
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    void testHandleReturnToConsider() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleReturnToConsider(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.DOWNLOADED || pictureState.getStatus() == PictureStatus.REJECTED) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertTrue("Thumbnail must become safe", result.getStatus() == PictureStatus.CONSIDERING);
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    void testHandleReject() {
        // arrange
        def allStatusesAndBrokenPermutations = getAllStatusesAndBrokenPermutations();

        // act
        def allStatusesAndBrokenPermutationsResult = allStatusesAndBrokenPermutations.collectEntries {
            pictureState, picture ->
                [pictureState, testPicture(
                        picture, { pic -> pictureHandler.handleReject(pic)}
                )]
        }

        // assert
        allStatusesAndBrokenPermutationsResult.each {pictureState, result ->
            if (pictureState.getStatus() == PictureStatus.CONSIDERING || pictureState.getStatus() == PictureStatus.DOWNLOADED) {
                assertTrue("There mustn't be an exception", result instanceof InterfaceliftPicture);
                assertTrue("Thumbnail must become safe", result.getStatus() == PictureStatus.REJECTED);
            } else {
                assertTrue("There must be an exception", result instanceof IllegalArgumentException);
            }
        }
    }

}
