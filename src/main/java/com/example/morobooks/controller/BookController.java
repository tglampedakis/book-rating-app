package com.example.morobooks.controller;

import com.example.morobooks.dto.*;
import com.example.morobooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Validated
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Controller API for retrieving book info from the external API.
     * Part 1.
     */
    @GetMapping()
    public ResponseEntity<BooksResponseDto> searchBooks(
            @RequestParam(value = "author_year_start", required = false) String authorYearStart,
            @RequestParam(value = "author_year_end", required = false) String authorYearEnd,
            @RequestParam(value = "languages", required = false) String languages,
            @RequestParam(value = "ids", required = false) String ids,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "pageSize", required = false) String pageSize
    ) {
        return new ResponseEntity<>(bookService.searchBooks(authorYearStart, authorYearEnd, languages, ids, search, page, pageSize),
                HttpStatus.OK);
    }

    /**
     * Controller API for adding a book review.
     * Part 2.
     */
    @PostMapping("/review")
    public ResponseEntity<GenericApiResponseDto> addBookReview(
            @Valid @RequestBody BookReviewRequestDto bookReviewRequest
    ) {
        return new ResponseEntity<>(bookService.addBookReview(bookReviewRequest), HttpStatus.OK);
    }

    /**
     * Controller API for retrieving already reviewed book info.
     * Part 3.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<BookDto> getBookDetails(
            @PathVariable("id") String bookId
    ) {
        return new ResponseEntity<>(bookService.getBookDetails(bookId), HttpStatus.OK);
    }

    /**
     * Controller API for retrieving N top-rated books.
     * Bonus Objective.
     */
    @GetMapping("/best-sellers")
    public ResponseEntity<List<BookDto>> getBestSellerBooks(
            @RequestParam(value = "threshold", defaultValue = "10") Integer threshold
    ) {
        return new ResponseEntity<>(bookService.getBestSellerBooks(threshold), HttpStatus.OK);
    }

    /**
     * Controller API for retrieving a book's monthly average rating.
     * Bonus Objective.
     */
    @GetMapping("/{id}/monthly-rating")
    public ResponseEntity<List<BookMonthlyAvgRatingDto>> getBookAvgMonthlyRating(
            @PathVariable(value = "id") String id
    ) {
        return new ResponseEntity<>(bookService.getBookAvgMonthlyRating(id), HttpStatus.OK);
    }

}
