package com.gallery.model.file;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * FileRepository class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:36 [Friday]
 */
public interface FileRepository extends CrudRepository<File, Long> { }
