package com.example.local.repository;


import com.example.local.entity.Image;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {


}
