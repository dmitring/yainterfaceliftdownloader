package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * It's common domain application class. It representsa interfacelift picture.
 */
@Entity
@Access(AccessType.FIELD)
@Data
@NoArgsConstructor
public class InterfaceliftPicture
{
    @Id
    @Column
    private String interfaceliftId;

    @Column
    private String title;

    @Column
    @Enumerated(EnumType.STRING)
    private PictureStatus status;

    @Column
    private int errorCount;

    @AttributeOverrides({
            @AttributeOverride(name="downloadUrl", column=@Column(name="thumbnail_download_url")),
            @AttributeOverride(name="filePath", column=@Column(name="thumbnail_file_path")),
            @AttributeOverride(name="fileMd5HexHash", column=@Column(name="thumbnail_file_md5_hex_hash"))
    })
    @Access(AccessType.PROPERTY)
    @Embedded
    private Picture thumbnail;


    @AttributeOverrides({
            @AttributeOverride(name="downloadUrl", column=@Column(name="full_download_url")),
            @AttributeOverride(name="filePath", column=@Column(name="full_file_path")),
            @AttributeOverride(name="fileMd5HexHash", column=@Column(name="full_file_md5_hex_hash"))
    })
    @Access(AccessType.PROPERTY)
    @Embedded
    private Picture fullPicture;

    public void increaseErrorCount() {
        errorCount++;
    }
}
