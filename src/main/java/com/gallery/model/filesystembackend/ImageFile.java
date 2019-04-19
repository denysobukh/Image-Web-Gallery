package com.gallery.model.filesystembackend;


import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Objects;

/**
 * ImageFile class represents image file
 * consist of:
 * source sourcePath to the image
 * sourcePath to the thumbnail
 * thumbnailState represents if the thumbnail exists, needed, not needed;
 *
 * @Non-thread safe
 */
@Entity
@Table(name = "files")
public class ImageFile implements Serializable {

    @GeneratedValue
    @Id
    private long id;
    @Id
    private String sourcePath;
    private String thumbnailPath;
    private ThumbnailState thumbnailState;
    /**
     * milliseconds since Epoch
     */
    private Long lastModified;
    @Transient
    private int hashcode;

    ImageFile() {
    }


    private ImageFile(String sourcePath, String thumbnailPath, long lastModified) {
        this.sourcePath = sourcePath;
        this.thumbnailPath = thumbnailPath;
        this.lastModified = lastModified;
        updateState();
    }

    public static ImageFile build(Path sourcePath) throws IllegalArgumentException {
        File f = sourcePath.toAbsolutePath().toFile();
        if (!f.exists()) throw new IllegalArgumentException(sourcePath.toAbsolutePath() + " file does not exist");
        return new ImageFile(f.getAbsolutePath(), null, f.lastModified());
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public ThumbnailState getThumbnailState() {
        return thumbnailState;
    }

    private void setThumbnailState(ThumbnailState thumbnailState) {
        this.thumbnailState = thumbnailState;
    }

    private void updateState() {
        if (sourcePath == null || thumbnailPath == null) {
            thumbnailState = ThumbnailState.NEEDED;
        } else if (Objects.equals(sourcePath, thumbnailPath)) {
            thumbnailState = ThumbnailState.SAME_AS_SOURCE;
        } else {
            thumbnailState = ThumbnailState.EXISTS;
        }
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
        updateState();
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
        updateState();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof ImageFile)) return false;
        final ImageFile o = (ImageFile) obj;
        return Objects.equals(sourcePath, o.sourcePath) && Objects.equals(thumbnailPath, o.thumbnailPath);
    }

    @Override
    public int hashCode() {
        int result = hashcode;
        if (result == 0) {
            result = (sourcePath == null ? 0 : sourcePath.hashCode());
            result = 31 * result + (thumbnailPath == null ? 0 : thumbnailPath.hashCode());
            hashcode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        LocalDateTime lastModifiedDateTime = LocalDateTime.ofEpochSecond(
                lastModified / 1000,
                0,
                ZoneOffset.ofTotalSeconds(
                        Calendar.getInstance().getTimeZone().getRawOffset() / 1000));
        return String.format("ImageFile[%d; %s; %s; %s]", id, sourcePath, thumbnailPath, lastModifiedDateTime);
    }

    enum ThumbnailState {
        NEEDED, EXISTS, SAME_AS_SOURCE
    }
}
