package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;

@Data
public class PictureInfo {
    private final String id;
    private final String title;
    private final String thumbnailUrlString;
    private final String fullUrlString;

    public PictureInfo(String id, String title, String thumbnailUrlString, String fullUrlString) {
        this.id = id;
        this.title = title;
        this.thumbnailUrlString = thumbnailUrlString;
        this.fullUrlString = fullUrlString;
    }
}
