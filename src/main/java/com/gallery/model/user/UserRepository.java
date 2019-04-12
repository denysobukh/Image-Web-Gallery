package com.gallery.model.user;

import org.springframework.data.repository.CrudRepository;

/**
 * UserRepository class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 13:06 [Friday]
 */
public interface UserRepository extends CrudRepository<User, Integer> {
}
