package com.example.local.services.impl;

import com.example.local.entity.Image;
import com.example.local.services.SearchingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchServiceImpl implements SearchingService {
    private String[] extensions = {"jpg"};
    @Value("${upload.path}")
    String uploadPath;
    @Override
    public List<Image> searchPhotos() throws IOException {
        String osName = System.getProperty("os.name");
        List<Image> files = findFiles(Paths.get(uploadPath), extensions);

        return files;
    }

    public List<Image> findFiles(Path path, String[] fileExtensions) throws IOException {
        long start = System.currentTimeMillis();
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        int length = uploadPath.toCharArray().length;

        List<Image> result;
        try (Stream<Path> walk = Files.walk(path, 2)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(path1 -> path1.toAbsolutePath())
                    // convert path to string
                    .map(p -> p.toString().substring(length))
                    .filter(f -> isEndWith(f, fileExtensions))
                    .map((String filename) -> Image.builder().filename(filename).build())
                    .collect(Collectors.toList());
        }
//        Files.getFileStore()
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.printf("time for searching is %s millis\n", time);
//        createImageShortCut(result);
        return result;

    }

//    private static void createImageShortCut(List<String> result) {
//        AtomicReference<BufferedImage> bufferedImage = null;
//        int width = 963;
//        int height = 640;
//
//        result.stream().forEach(fileAddres ->
//        {
//            try {
//                File file = new File(fileAddres);
//                bufferedImage.set(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
//                bufferedImage.set(ImageIO.read(file));
//                bufferedImage.
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//    }

    private static boolean isEndWith(String file, String[] fileExtensions) {
        boolean result = false;
        for (String fileExtension : fileExtensions) {
            if (file.endsWith(fileExtension)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
