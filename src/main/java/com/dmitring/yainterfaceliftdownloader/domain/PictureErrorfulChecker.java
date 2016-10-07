package com.dmitring.yainterfaceliftdownloader.domain;

/**
 * A criteria to change InterfaceliftPicture status to ERRORFUL
 */
public interface PictureErrorfulChecker {
    boolean isPictureErrorful(InterfaceliftPicture picture);
}
