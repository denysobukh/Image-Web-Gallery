package com.gallery.model;

import java.util.List;

/**
 * Project: Gallery
 * Class: MenuItem
 * Date: 2019-03-24 09:23 [Sunday]
 * <p>
 * Represents the Item of the Menu to be rendered in Views
 *
 * @author Dennis Obukhov
 */
public class MenuItem {

    private String name;
    private String url;
    private MenuItem parent;
    private List<MenuItem> children;

    public MenuItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public MenuItem(String name, String url, List<MenuItem> children) {
        this.name = name;
        this.url = url;
        this.children = children;
        for (MenuItem child : children) {
            child.parent = this;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public MenuItem getParent() {
        return parent;
    }

    public List<MenuItem> getChildren() {
        return children;
    }
}
