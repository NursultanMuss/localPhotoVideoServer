package com.example.local.controllers;

import com.example.local.entity.Image;
import com.example.local.repository.ImageRepository;
import com.example.local.services.SearchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller
public class MainController {
    @Autowired
    SearchingService service;

    @Value("${upload.path}")
    String uploadPath;

    @Autowired
    ImageRepository repository;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "main";
    }

    @GetMapping("/main")
    public String findPhotoAndVideo(Model model) throws IOException {
//        List<String> imagesList = service.searchPhotos();
        Iterable<Image> images = repository.findAll();

        model.addAttribute("images",  images);
        return "s";
    }

    @PostMapping("/main")
    public String add(
            Map<String, Object> model,
            @RequestParam("file") MultipartFile file) throws IOException {
        String resultFileName = "";
        if (file != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdir();

            String uuidFileName = UUID.randomUUID().toString();
            resultFileName = uuidFileName + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));
        }
        repository.save(Image.builder().filename(resultFileName).build());
        Iterable<Image> images = repository.findAll();
        model.put("images",  images);

        return "s";
    }
}
