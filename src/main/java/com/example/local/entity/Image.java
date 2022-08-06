package com.example.local.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "image_list")
public class Image {

    @Id
    private String id;

    private String filename;
}
