package com.gallery.model.image;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * ImagePagingAndSortingRepository class
 *
 * @author Dennis Obukhov
 * @date 2019-05-02 16:45 [Thursday]
 */
public interface ImagePagingAndSortingRepository extends PagingAndSortingRepository<Image, Long> {
}
