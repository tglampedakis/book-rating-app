package com.example.morobooks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsDto {

    private Long id;
    private String title;
    @JsonProperty("download_count")
    private Long downloadCount;
    private Double rating;

}
