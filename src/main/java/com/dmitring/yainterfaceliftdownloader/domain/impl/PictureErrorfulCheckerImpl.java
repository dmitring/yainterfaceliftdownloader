package com.dmitring.yainterfaceliftdownloader.domain.impl;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureErrorfulChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PictureErrorfulCheckerImpl implements PictureErrorfulChecker {

    @Value("${com.dmitring.yainterfaceliftdownloader.pictureDownloadAttemptCount}")
    protected int allowErrors;

    public boolean isPictureErrorful(InterfaceliftPicture picture) {
        return picture.getErrorCount() >= allowErrors;
    }
}
