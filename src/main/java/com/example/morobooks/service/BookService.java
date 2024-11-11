package com.example.morobooks.service;

import com.example.morobooks.dto.*;

import java.util.List;


public interface BookService {

    BooksResponseDto searchBooks(String authorYearStart, String authorYearEnd, String languages,
                                 String ids, String search, String page, String pageSize);

    GenericApiResponseDto addBookReview(BookReviewRequestDto bookReviewRequest);

    BookDto getBookDetails(String bookId);

    List<BookDto> getBestSellerBooks(Integer threshold);

    List<BookMonthlyAvgRatingDto> getBookAvgMonthlyRating(String id);

}
