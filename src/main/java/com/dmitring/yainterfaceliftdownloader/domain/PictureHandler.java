package com.dmitring.yainterfaceliftdownloader.domain;

/**
 * The interface offers actions and transformations on InterfaceliftPicture class
 */
public interface PictureHandler {
    InterfaceliftPicture createNewPicture(String id, String tittle, String thumbnailUrl, String fullPictureUrl);
    void handleThumbnailSuccessDownload(InterfaceliftPicture picture);
    void handleThumbnailSuccessRepair(InterfaceliftPicture picture);
    void handleThumbnailFailedDownload(InterfaceliftPicture picture);
    void handleFullPictureSuccessDownload(InterfaceliftPicture picture);
    void handleFullPictureSuccessRepair(InterfaceliftPicture picture) ;
    void handleFullPictureFailedDownload(InterfaceliftPicture picture);
    void handleAccept(InterfaceliftPicture picture);
    void handleReturnToConsider(InterfaceliftPicture picture);
    void handleReject(InterfaceliftPicture picture);
    String getThumbnailFilePath(InterfaceliftPicture picture);
    String getFullPictureFilePath(InterfaceliftPicture picture);
}
