package com.example.local.controllers;

import com.example.local.entity.Image;
import com.example.local.repository.ImageRepository;
import com.example.local.services.SearchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


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
        return "s";
    }

    @GetMapping(value = "/main",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String findPhotoAndVideo(
            @RequestParam(required = false, defaultValue = "") Map<String, Object> filter,
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable)
            throws IOException, ExecutionException, InterruptedException {
        Page<Image> page;
        if(filter != null && !filter.isEmpty())
            page =  service.mainPhotos(filter, pageable);
        else
            page = repository.findAll(pageable);

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
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
        model.put("images", images);

        return "s";
    }
}
