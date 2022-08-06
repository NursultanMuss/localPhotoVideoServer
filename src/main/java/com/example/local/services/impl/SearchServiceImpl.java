package com.example.local.services.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.local.entity.Image;
import com.example.local.repository.ImageRepository;
import com.example.local.services.SearchingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchingService {
    public static final String WINDOWS = "Windows";
    private String[] extensions = {"jpg", "JPG"};
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${windows.user.path}")
    private String windowsPath;
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Override
    @Async
    public CompletableFuture<List<Image>> searchPhotos() throws IOException {
        String osName = System.getProperty("os.name");
        String homePath = "";
        if(osName.startsWith(WINDOWS)){
            homePath = windowsPath;
        }else{
            homePath = uploadPath;
        }
        List<Image> files = findFiles(Paths.get(homePath), extensions, homePath);

        return CompletableFuture.completedFuture(files);
    }

    @Override
    public Page<Image> mainPhotos(Map<String, Object> filter, Pageable pageable){
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(0, 20))
                .build();
        SearchHits<Image> searchHits = elasticsearchOperations.search(nativeSearchQuery, Image.class, IndexCoordinates.of("image_list"));
        List<Image> collect = searchHits.get().filter(searchHit -> Objects.nonNull(searchHit.getContent())).map(SearchHit::getContent).collect(Collectors.toList());
        PageImpl<Image> images = new PageImpl<>(collect, pageable, collect.size());
        return images;
    }

    public List<Image> findFiles(Path path, String[] fileExtensions, String homePath) throws IOException {
        long start = System.currentTimeMillis();
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        int length = homePath.toCharArray().length;

        List<Image> result = new ArrayList<>();

        try {
            Files.walkFileTree(
                    path,
                    new HashSet<FileVisitOption>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)),
                    4, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {
                            System.out.printf("Visiting file %s\n", file);
                            String path = Optional.of(file)
                                    .filter(p -> !Files.isDirectory(p))
                                    .map(path1 -> path1.toAbsolutePath())
//                    // convert path to string
                                    .map(p -> p.toString().substring(length))
                                    .filter(f -> isEndWith(f, fileExtensions))
                                    .orElse(null);
                            if (path != null) {
                                path = path.replace("\\","/");
                                if(!path.startsWith("/"))
                                    path = "/" + path;
                                //read the image file
//                                String pathString = file.toAbsolutePath().toString();
//                                File imgFile = new File(pathString);
//                                getMetaDataV2(imgFile);

                                //save image to repository
                                saveImgInRepository(path);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException e)
                                throws IOException {
                            System.err.printf("Visiting failed for %s\n", file);
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir,
                                                                 BasicFileAttributes attrs)
                                throws IOException {
                            System.out.printf("About to visit directory %s\n", dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            log.error("Error while reading files - {}", e.getMessage(), e);
//            throw e;
        }

//        Files.getFileStore()
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.printf("time for searching is %s millis\n", time);
//        createImageShortCut(result);
        return result;

    }

    private void saveImgInRepository(String path) {
        Image image = Image.builder().filename(path).build();
        Optional<Image> byFileName = imageRepository.findByFilename(path);
        if(!byFileName.isPresent())
            imageRepository.save(image);
        else
            log.info("There exists same note with that image path - {}", path);
    }

//    @SneakyThrows
//    private void getMetadata(File imgFile) {
//        Metadata metadata = ImageMetadataReader.readMetadata(imgFile);
//        long length = imgFile.length();
//        long freeSpace = imgFile.getFreeSpace();
//        long lastModified = imgFile.lastModified();
//        log.info("length - {}, freeSpace - {}, lastModified - {}", length, freeSpace, lastModified);


//        for (Directory directory : metadata.getDirectories()) {
//            for (Tag tag : directory.getTags()) {
////                System.out.format("[%s] - %s = %s",
////                        directory.getName(), tag.getTagName(), tag.getDescription());
//            }
//            if (directory.hasErrors()) {
//                for (String error : directory.getErrors()) {
//                    System.err.format("ERROR: %s", error);
//                }
//            }
//        }
//    }

//    @SneakyThrows
//    private void getMetaDataV2(File file){
//        ImageInputStream iis = ImageIO.createImageInputStream(file);
//        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
//
//        if (readers.hasNext()) {
//
//            // pick the first available ImageReader
//            ImageReader reader = readers.next();
//
//            // attach source to the reader
//            reader.setInput(iis, true);
//
//            // read metadata of first image
//            IIOMetadata metadata = reader.getImageMetadata(0);
//
//            String[] names = metadata.getMetadataFormatNames();
//            int length = names.length;
//            for (int i = 0; i < length; i++) {
//                System.out.println( "Format name: " + names[ i ] );
//                displayMetadata(metadata.getAsTree(names[i]));
//            }
//        }
//    }

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
