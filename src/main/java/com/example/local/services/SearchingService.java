package com.example.local.services;


import com.example.local.entity.Image;

import java.io.IOException;
import java.util.List;

public interface SearchingService {
    List<Image> searchPhotos() throws IOException;


}
