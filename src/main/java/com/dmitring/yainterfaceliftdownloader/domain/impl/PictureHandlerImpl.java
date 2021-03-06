package com.dmitring.yainterfaceliftdownloader.domain.impl;

import com.dmitring.yainterfaceliftdownloader.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PictureHandlerImpl implements PictureHandler {
    private static final Logger log = Logger.getLogger(PictureHandlerImpl.class.getName());

    @Value("${com.dmitring.yainterfaceliftdownloader.thumbnailPrefixPath}")
    private String thumbnailPrefixPath;

    @Value("${com.dmitring.yainterfaceliftdownloader.fullPicturePrefixPath}")
    private String fullPicturePrefixPath;

    @Value("${com.dmitring.yainterfaceliftdownloader.pictureFileExtension}")
    private String pictureFileExtension;

    private final PictureErrorfulChecker pictureErrorfulChecker;

    @Autowired
    public PictureHandlerImpl(PictureErrorfulChecker pictureErrorfulChecker) {
        this.pictureErrorfulChecker = pictureErrorfulChecker;
    }

    @Override
    public InterfaceliftPicture createNewPicture(String id, String tittle, String thumbnailUrl, String fullPictureUrl) {
        InterfaceliftPicture creatingPicture = new InterfaceliftPicture();
        creatingPicture.setInterfaceliftId(id);
        creatingPicture.setTitle(tittle);
        creatingPicture.setThumbnail(new Picture(thumbnailUrl));
        creatingPicture.setFullPicture(new Picture(fullPictureUrl));
        creatingPicture.setStatus(PictureStatus.JUST_FOUND);
        return creatingPicture;
    }

    protected boolean canDownloadThumbnail(InterfaceliftPicture picture) {
        return picture.getStatus() == PictureStatus.JUST_FOUND;
    }

    @Override
    public void handleThumbnailSuccessDownload(InterfaceliftPicture picture) {
        final Picture thumbnail = picture.getThumbnail();

        if (!canDownloadThumbnail(picture)) {
            throwIllegalArgumentThumbnail(picture.toString());
        }

        picture.setStatus(PictureStatus.CONSIDERING);
        thumbnail.setFilePath(getThumbnailFilePath(picture));
    }

    protected boolean canRepairThumbnail(InterfaceliftPicture picture) {
        return (picture.getThumbnail().isBroken());
    }

    @Override
    public void handleThumbnailSuccessRepair(InterfaceliftPicture picture) {
        final Picture thumbnail = picture.getThumbnail();

        if (!canRepairThumbnail(picture)) {
            throwIllegalArgumentThumbnail(picture.toString());
        }

        thumbnail.setBroken(false);
    }

    @Override
    public void handleThumbnailFailedDownload(InterfaceliftPicture picture) {
        if (!canDownloadThumbnail(picture) && !canRepairThumbnail(picture)) {
            throwIllegalArgumentThumbnail(picture.toString());
        }

        picture.increaseErrorCount();
        log.warning(String.format("Picture has been unsuccessfully downloaded %s", picture.toString()));
        if (pictureErrorfulChecker.isPictureErrorful(picture)) {
            picture.setStatus(PictureStatus.ERRORFUL);
            log.warning(String.format("Picture downloading has been blocked due to downloading errors %s", toString()));
        }
    }

    protected boolean canDownloadFullPicture(InterfaceliftPicture picture) {
        return picture.getStatus() == PictureStatus.ACCEPTED;
    }

    @Override
    public void handleFullPictureSuccessDownload(InterfaceliftPicture picture) {
        final Picture fullPicture = picture.getFullPicture();

        if (!canDownloadFullPicture(picture)) {
            throwIllegalArgumentFullPicture(picture.toString());
        }

        picture.setStatus(PictureStatus.DOWNLOADED);
        fullPicture.setFilePath(getFullPictureFilePath(picture));
    }

    protected boolean canRepairFullPicture(InterfaceliftPicture picture) {
        return (picture.getFullPicture().isBroken());
    }

    @Override
    public void handleFullPictureSuccessRepair(InterfaceliftPicture picture) {
        final Picture fullPicture = picture.getFullPicture();

        if (!canRepairFullPicture(picture)) {
            throwIllegalArgumentFullPicture(picture.toString());
        }

        fullPicture.setBroken(false);
    }

    @Override
    public void handleFullPictureFailedDownload(InterfaceliftPicture picture) {
        if (!canDownloadFullPicture(picture) && !canRepairFullPicture(picture)) {
            throwIllegalArgumentFullPicture(picture.toString());
        }

        picture.increaseErrorCount();
        log.warning(String.format("Picture has been unsuccessfully downloaded %s", picture.toString()));
        if (pictureErrorfulChecker.isPictureErrorful(picture)) {
            picture.setStatus(PictureStatus.ERRORFUL);
            log.warning(String.format("Picture downloading has been blocked due to downloading errors %s", toString()));
        }
    }

    protected boolean canAccept(InterfaceliftPicture picture) {
        return (picture.getStatus() == PictureStatus.CONSIDERING || picture.getStatus() == PictureStatus.REJECTED);
    }

    @Override
    public void handleAccept(InterfaceliftPicture picture) {
        if (!canAccept(picture))
            throw new IllegalArgumentException(String.format("It doesn't allow to accept picture" +
                            "Check picture state. %s",
                    picture.toString()));

        picture.setStatus(PictureStatus.ACCEPTED);
    }

    protected boolean canReturnedToConsider(InterfaceliftPicture picture) {
        return (picture.getStatus() == PictureStatus.DOWNLOADED || picture.getStatus() == PictureStatus.REJECTED);
    }

    @Override
    public void handleReturnToConsider(InterfaceliftPicture picture) {
        if (!canReturnedToConsider(picture))
            throw new IllegalArgumentException(String.format("It doesn't allow to return picture to consider" +
                            "Check picture state. %s",
                    picture.toString()));

        picture.setStatus(PictureStatus.CONSIDERING);
    }

    protected boolean canReject(InterfaceliftPicture picture) {
        return (picture.getStatus() == PictureStatus.DOWNLOADED || picture.getStatus() == PictureStatus.CONSIDERING);
    }

    @Override
    public void handleReject(InterfaceliftPicture picture) {
        if (!canReject(picture))
            throw new IllegalArgumentException(String.format("It doesn't allow to reject picture" +
                            "Check picture state. %s",
                    picture.toString()));

        picture.setStatus(PictureStatus.REJECTED);
    }

    protected String constructFilePath(String prefixPath, String pictureId) {
        return prefixPath +
                pictureId +
                pictureFileExtension;
    }

    @Override
    public String getThumbnailFilePath(InterfaceliftPicture picture) {
        return constructFilePath(thumbnailPrefixPath, picture.getInterfaceliftId());
    }

    @Override
    public String getFullPictureFilePath(InterfaceliftPicture picture) {
        return constructFilePath(fullPicturePrefixPath, picture.getInterfaceliftId());
    }

    protected void throwIllegalArgumentThumbnail(String pictureString) {
        throw new IllegalArgumentException(String.format("It doesn't allow to download thumbnail. " +
                        "Check picture state and thumbnail's broken state. %s",
                pictureString));
    }

    protected void throwIllegalArgumentFullPicture(String pictureString) {
        throw new IllegalArgumentException(String.format("It doesn't allow to download full picture. " +
                        "Check picture state and full picture's broken state. %s",
                pictureString));
    }
}
