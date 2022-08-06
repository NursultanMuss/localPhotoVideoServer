package com.example.local.services;


import com.example.local.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SearchingService {
    CompletableFuture<List<Image>> searchPhotos() throws IOException;
    Page<Image> mainPhotos(Map<String, Object> filter, Pageable pageable);


}
