package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;

import java.util.Collection;

/**
 * It's a class that includes results of parsed web page
 */
@Data
public class ParsedPage {
    private final Collection<PictureInfo> pictureInfo;
    private final boolean nextPageExists;
}
