package com.dmitring.yainterfaceliftdownloader.services;

import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo;

public interface NewPictureFoundHandlerService {
    void init();
    void handleImage(PictureInfo pictureInfo);
    boolean shouldContinue();
    void handleFinish();
}