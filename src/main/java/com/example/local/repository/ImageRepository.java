package com.example.local.repository;


import com.example.local.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image, String> {
    Optional<Image> findByFilename(String fileName);

    Page<Image> findAll(Pageable pageable);

    @Override
    <S extends Image> S save(S entity);
}
