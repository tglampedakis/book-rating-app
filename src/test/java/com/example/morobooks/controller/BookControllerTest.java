package com.example.morobooks.controller;

import com.example.morobooks.dto.*;
import com.example.morobooks.exception.BookNotFoundException;
import com.example.morobooks.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void searchBooks_ShouldReturnBooksResponseDto() throws Exception {
        BooksResponseDto responseDto = new BooksResponseDto(List.of(new BookDto(
                1L,
                "Test Book",
                List.of(new AuthorDto("Test Author", 1900, 1950)),
                List.of("en"),
                5000L,
                4.8,
                List.of("Test review 1", "Test review 2")
        )));

        when(bookService.searchBooks(any(), any(), any(), any(), any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/books")
                        .param("search", "Test Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("Test Book"));
    }

    @Test
    void addBookReview_ShouldAddReviewSuccessfully() throws Exception {
        GenericApiResponseDto responseDto = new GenericApiResponseDto(true, "Review added successfully");

        when(bookService.addBookReview(any(BookReviewRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/books/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1, \"rating\":4.5, \"review\":\"Test review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Review added successfully"));
    }

    @Test
    void getBookDetails_ShouldReturnBookDto() throws Exception {
        BookDto bookDto = new BookDto(
                1L,
                "Test Book",
                List.of(new AuthorDto("Test author", 1900, 1950)),
                List.of("en"),
                5000L,
                4.8,
                List.of("Test review 1", "Test review 2")
        );

        when(bookService.getBookDetails("1")).thenReturn(bookDto);

        mockMvc.perform(get("/api/v1/books/details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBestSellerBooks_ShouldReturnBestSellerBooks() throws Exception {
        List<BookDto> bestSellers = List.of(new BookDto(
                1L,
                "Test Book",
                List.of(new AuthorDto("Author Test", 1900, 1950)),
                List.of("en"),
                5000L,
                4.8,   // rating
                List.of("Test review 1", "Test review 2")
        ));

        when(bookService.getBestSellerBooks(10)).thenReturn(bestSellers);

        mockMvc.perform(get("/api/v1/books/best-sellers")
                        .param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void getBookAvgMonthlyRating_ShouldReturnMonthlyAvgRating() throws Exception {
        List<BookMonthlyAvgRatingDto> avgRatings = List.of(new BookMonthlyAvgRatingDto("2023-10", 4.5));

        when(bookService.getBookAvgMonthlyRating("1")).thenReturn(avgRatings);

        mockMvc.perform(get("/api/v1/books/1/monthly-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("2023-10"))
                .andExpect(jsonPath("$[0].averageRating").value(4.5));
    }
}
