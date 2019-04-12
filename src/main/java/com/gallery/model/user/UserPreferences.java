package com.gallery.model.user;

import org.hibernate.annotations.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.nio.file.Path;

/**
 * Project: Gallery
 * Class: UserPreferences
 * Date: 2019-03-28 15:20 [Thursday]
 * <p>
 * Represents user's preferences
 *
 * @author Dennis Obukhov
 */

@Component
@Scope(value = "prototype")
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = WebApplicationContext.SCOPE_SESSION)
@Entity
public class UserPreferences {

    @Id
    @GeneratedValue
    private int id;

    @Type(type = "com.gallery.model.file.PathType")
    //@Columns(columns = {@Column(name = "current_dir")})
    private Path currentDir;

    public UserPreferences() {
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }

    @Override
    public String toString() {
        return String.format("Preference[%d; curDir=%s]", id, currentDir);
    }
}
