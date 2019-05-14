package com.gallery.model.directory;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Directory class represents Directory entity
 *
 * @author Dennis Obukhov
 * @date 2019-04-29 13:02 [Monday]
 * @ThreadSafe
 */

@Entity
@Table(name = "directories")
public class Directory implements Comparable<Directory> {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @NaturalId
    private String path;
    @NaturalId
    private String Uri;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @SortNatural
    private SortedSet<Directory> children = new TreeSet<>();
    private long imagesCount;
    private boolean listed;
    private boolean isRoot;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Directory parent;
    @Transient
    private int hashCode;
    public Directory(String name, String path) {
        this.name = name;
        this.path = path;
    }
    public Directory() {
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public long getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(long imagesCount) {
        this.imagesCount = imagesCount;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isListed() {
        return listed;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SortedSet<Directory> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<Directory> children) {
        this.children = children;
    }

    public void addChild(Directory child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("Directory (%-3d; %-12s; %s", id, name, path));

        if (children.size() > 0) {
            sb.append(String.format("%n{"));
            for (Directory d : children) {
                sb.append(String.format("%n\t%s", d));
            }

            sb.append(String.format("%n}"));
        }
        sb.append(String.format(")"));
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof Directory)) return false;
        final Directory o = (Directory) obj;
        return Objects.equals(path, o.path);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = path == null ? 0 : path.hashCode();
            hashCode = result;
        }
        return result;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(Directory o) {
        int c = 0;
        if (name != null && o.name != null) {
            c = String.CASE_INSENSITIVE_ORDER.compare(name, o.name);
        } else {
            c = Long.compare(id, o.id);
        }
        return c;
    }
}
