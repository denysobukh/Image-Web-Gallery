package com.gallery.model.image;


import com.gallery.model.directory.Directory;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Objects;

/**
 * image class represents image directory
 * consist of:
 * source source to the image
 * source to the thumbnail
 *
 * @Non-thread safe
 */
@Entity
@Table(name = "files")
public class Image implements Serializable {

    @Id
    @GeneratedValue
    private long id;
    @NaturalId
    private String source;
    private String thumbnail;
    /**
     * Last modified time in milliseconds since Epoch
     */
    private long modified;
    @Transient
    private int hashcode;

    protected Image() {
    }

    private Image(String source, String thumbnail, Directory directory, long modified) {
        this.source = source;
        this.thumbnail = thumbnail;
        this.modified = modified;
    }

    public Image(String source) {
        this.source = source;
    }

    public long getId() {
        return id;
    }

    public Long getModified() {
        return modified;
    }

    String getSource() {
        return source;
    }

    String getThumbnail() {
        return thumbnail;
    }

    void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof Image)) return false;
        final Image o = (Image) obj;
        return Objects.equals(source, o.source) && modified == o.modified;
    }

    @Override
    public int hashCode() {
        int result = hashcode;
        if (result == 0) {
            result = 31 * (source == null ? 0 : source.hashCode());
            result = 31 * result + Long.hashCode(modified);
            hashcode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        LocalDateTime lastModifiedDateTime = LocalDateTime.ofEpochSecond(
                modified / 1000,
                0,
                ZoneOffset.ofTotalSeconds(
                        Calendar.getInstance().getTimeZone().getRawOffset() / 1000));
        return String.format("image[%d; %s; %s; %s]", id, source, thumbnail, lastModifiedDateTime);
    }
}
