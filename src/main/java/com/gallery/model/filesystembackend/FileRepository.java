package com.gallery.model.filesystembackend;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * FileRepository class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:36 [Friday]
 */
public interface FileRepository extends CrudRepository<ImageFile, Long> {
    List<ImageFile> findBySourcePath(String sourcePath);
}
