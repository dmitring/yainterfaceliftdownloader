package com.dmitring.yainterfaceliftdownloader.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PictureErrorfulChecker {

    @Value("${com.dmitring.yainterfaceliftdownloader.pictureDownloadAttemptCount}")
    protected int allowErrors;

    boolean isPictureErrorful(InterfaceliftPicture picture) {
        return picture.getErrorCount() >= allowErrors;
    }
}
