package com.gallery.model.file;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue
    private long id;

    private String path;
    private String thumbPath;

    public File() {
    }

    public File(String path, String thumbPath) {
        this.path = path;
        this.thumbPath = thumbPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof File)) return false;
        File o = (File) obj;
        return Objects.equals(path, o.path) && Objects.equals(thumbPath, o.thumbPath);
    }

    @Override
    public int hashCode() {
        int result = (path == null ? 0 : path.hashCode());
        result = 31 * result + (thumbPath == null ? 0 : thumbPath.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("File[%d; %s; %s]", id, path, thumbPath);
    }
}
