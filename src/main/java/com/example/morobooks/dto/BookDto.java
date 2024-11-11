package com.example.morobooks.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto {

    private Long id;
    private String title;
    private List<AuthorDto> authors;
    private List<String> languages;
    @JsonProperty("download_count")
    private Long downloadCount;
    private Double rating;
    private List<String> reviews;

}
