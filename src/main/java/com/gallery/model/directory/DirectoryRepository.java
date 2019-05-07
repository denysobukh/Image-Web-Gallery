package com.gallery.model.directory;

import org.springframework.data.repository.CrudRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * DirectoryRepository class
 *
 * @author Dennis Obukhov
 * @date 2019-04-30 16:34 [Tuesday]
 */
public interface DirectoryRepository  extends CrudRepository<Directory, Long> {
    Set<Directory> findAll();
    Set<Directory> findByPath(String path);
}
