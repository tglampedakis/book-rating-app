package com.example.morobooks.feign;

import com.example.morobooks.dto.GutendexBooksResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
        name = "${gutendex.feign.name}",
        url = "${gutendex.feign.url}"
)
public interface GutendexFeignClient {

    @GetMapping(value = "/books")
    GutendexBooksResponseDto getAllBooks(
            @RequestParam("author_year_start") String authorYearStart,
            @RequestParam("author_year_end") String authorYearEnd,
            @RequestParam("languages") String languages,
            @RequestParam("ids") String ids,
            @RequestParam("search") String search,
            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize
    );

}
