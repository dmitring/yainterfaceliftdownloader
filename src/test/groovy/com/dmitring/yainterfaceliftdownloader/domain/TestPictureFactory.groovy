package com.dmitring.yainterfaceliftdownloader.domain

import com.dmitring.yainterfaceliftdownloader.domain.impl.PictureHandlerImpl

import static org.mockito.Mockito.mock

public class TestPictureFactory {
    PictureHandler pictureHandler = new PictureHandlerImpl(mock(PictureErrorfulChecker.class));
    int postfix = 0;

    def createPicture() {
        return pictureHandler.createNewPicture("picturesId_+${postfix}", "pictureTitle_+${postfix}", "test://thumbnail_+${postfix}", "test://fullPicture_+${postfix}");
        postfix++;
    }

    def createPictureWithBrokenState(def thumbnailBroken, def fullPictureBroken) {
        def picture = createPicture();
        if (thumbnailBroken)
            picture.getThumbnail().setBroken(true);
        if (fullPictureBroken)
            picture.getFullPicture().setBroken(true);
        return picture;
    }

    def createPictureWithStatus(def status) {
        def picture = createPicture();
        picture.setStatus(status);
        return picture;
    }
}