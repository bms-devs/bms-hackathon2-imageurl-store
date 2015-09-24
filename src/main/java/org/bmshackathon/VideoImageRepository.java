package org.bmshackathon;

import org.springframework.data.repository.Repository;

import java.util.Optional;

//@RepositoryRestResource
public interface VideoImageRepository extends Repository<VideoImage, Long> {

    Optional<VideoImage> findOne(Long id);

    Iterable<VideoImage> findAll();
}
