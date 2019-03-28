package com.gallery.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Path;

/**
 * Project: Gallery
 * Class: UserPreferences
 * Date: 2019-03-28 15:20 [Thursday]
 *
 * Represents user's preferences
 *
 * @author Dennis Obukhov
 */

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = WebApplicationContext.SCOPE_SESSION)
public class UserPreferences {
    private Path currentDir;

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }
}
