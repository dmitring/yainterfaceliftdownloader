package com.dmitring.yainterfaceliftdownloader.domain;

import java.util.Collection;

public class ParsedPage {

    private final Collection<PictureInfo> pictureInfo;
    private final boolean nextPageExists;

    public ParsedPage(Collection<PictureInfo> pictureInfo, boolean nextPageExists) {
        this.pictureInfo = pictureInfo;
        this.nextPageExists = nextPageExists;
    }

    public Collection<PictureInfo> getPictureInfo() {
        return pictureInfo;
    }

    public boolean doesNextPageExist() {
        return nextPageExists;
    }
}
