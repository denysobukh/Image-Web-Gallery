package com.gallery.model.image;

import com.gallery.model.directory.Directory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * ImageCrudRepository class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:36 [Friday]
 */
public interface ImageCrudRepository extends CrudRepository<Image, Long> {
    List<Image> findBySource(String source);
}
