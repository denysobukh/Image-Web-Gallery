package com.gallery.model.filesystembackend;


import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
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
 * state represents if the thumbnail exists, needed, not needed;
 *
 * @Non-thread safe
 */
@Entity
@Table(name = "files")
public class ImageFile implements Serializable {

    @Id
    @GeneratedValue
    private long id;
    @NaturalId
    private String sourcePath;
    private String thumbnailPath;
    private State state;
    /**
     * milliseconds since Epoch
     */
    private long lastModified;
    @Transient
    private int hashcode;

    protected ImageFile() {
    }

    private ImageFile(String sourcePath, String thumbnailPath, long lastModified) {
        this.sourcePath = sourcePath;
        this.thumbnailPath = thumbnailPath;
        this.lastModified = lastModified;
        updateState();
    }


    public static ImageFile build(Path sourcePath) throws IOException {
        File f = sourcePath.toAbsolutePath().toFile();
        if (!f.exists()) throw new IOException(sourcePath.toAbsolutePath() + " file does not exist");
        return new ImageFile(f.getAbsolutePath(), null, f.lastModified());
    }


    public long getId() {
        return id;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public State getState() {
        return state;
    }

    private void updateState() {
        if (sourcePath == null || thumbnailPath == null) {
            state = State.NEW;
        } else if (Objects.equals(sourcePath, thumbnailPath)) {
            state = State.SOURCE_THUMBNAIL;
        } else {
            state = State.HAS_THUMBNAIL;
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
        return Objects.equals(sourcePath, o.sourcePath) && lastModified == o.lastModified;
    }

    @Override
    public int hashCode() {
        int result = hashcode;
        if (result == 0) {
            result = 31 * (sourcePath == null ? 0 : sourcePath.hashCode());
            result = 31 * result + Long.hashCode(lastModified);
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

    enum State {
        NEW, HAS_THUMBNAIL, SOURCE_THUMBNAIL
    }
}
