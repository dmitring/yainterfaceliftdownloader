package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;

import java.util.Collection;

@Data
public class ParsedPage {
    private final Collection<PictureInfo> pictureInfo;
    private final boolean nextPageExists;
}
