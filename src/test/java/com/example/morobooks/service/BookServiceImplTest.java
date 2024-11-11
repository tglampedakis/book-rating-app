package com.example.morobooks.service;

import com.example.morobooks.dto.*;
import com.example.morobooks.entity.*;
import com.example.morobooks.exception.BookNotFoundException;
import com.example.morobooks.exception.ValidationException;
import com.example.morobooks.feign.GutendexFeignClient;
import com.example.morobooks.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private RatingsRepository ratingsRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private LanguagesRepository languagesRepository;
    @Mock
    private ReviewsRepository reviewsRepository;
    @Mock
    private GutendexFeignClient gutendexClient;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(gutendexClient, bookRepository, ratingsRepository, authorRepository, languagesRepository, reviewsRepository);
    }

    @Test
    void testSearchBooks_ShouldReturnBooks() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Test Book");
        List<BookDto> bookDtoList = List.of(bookDto);

        GutendexBooksResponseDto mockResponse = new GutendexBooksResponseDto();
        mockResponse.setResults(bookDtoList);

        when(gutendexClient.getAllBooks("2000", "2020", "en", null, "science", "1", "10"))
                .thenReturn(mockResponse);

        BooksResponseDto response = bookService.searchBooks("2000", "2020", "en", null, "science", "1", "10");

        assertNotNull(response);
        assertFalse(response.getBooks().isEmpty());
        assertEquals("Test Book", response.getBooks().get(0).getTitle());
    }

    @Test
    void testSearchBooks_ShouldThrowExceptionWhenNoBooksFound() {
        GutendexBooksResponseDto mockResponse = new GutendexBooksResponseDto();
        mockResponse.setResults(Collections.emptyList());

        when(gutendexClient.getAllBooks("2000", "2020", "en", null, "science", "1", "10"))
                .thenReturn(mockResponse);

        assertThrows(BookNotFoundException.class, () ->
                bookService.searchBooks("2000", "2020", "en", null, "science", "1", "10"));
    }

    @Test
    void testGetBookDetails_ShouldReturnBookDto() {
        Long bookId = 1L;
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle("Test Book");

        Ratings ratings = new Ratings();
        ratings.setId(1L);
        ratings.setBook(book);
        ratings.setRating(3.4);

        Authors authors = new Authors();
        authors.setId(1L);
        authors.setBook(book);
        authors.setName("Test author");
        authors.setBirthYear(1934);
        authors.setDeathYear(1934);

        Languages languages = new Languages();
        languages.setBook(book);
        languages.setId(1L);
        languages.setLanguage("en");

        Reviews reviews = new Reviews();
        reviews.setBook(book);
        reviews.setReviewId(1L);
        reviews.setReview("Test review!");

        book.setRatings(List.of(ratings));
        book.setAuthors(List.of(authors));
        book.setLanguages(List.of(languages));
        book.setReviews(List.of(reviews));

        when(bookRepository.findByBookId(bookId)).thenReturn(java.util.Optional.of(book));

        BookDto result = bookService.getBookDetails(String.valueOf(bookId));

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void testGetBookDetails_ShouldThrowExceptionWhenBookNotFound() {
        Long bookId = 2L;
        when(bookRepository.findByBookId(bookId)).thenReturn(java.util.Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookDetails(String.valueOf(bookId)));
    }

    @Test
    void testAddBookReview_ShouldAddReview() {
        BookReviewRequestDto reviewRequest = new BookReviewRequestDto();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(4.5);
        reviewRequest.setReview("Great Book!");

        Book book = new Book();
        book.setBookId(1L);

        Reviews reviews = new Reviews();
        reviews.setBook(book);
        reviews.setReviewId(1L);
        reviews.setReview("Test review!");

        Ratings ratings = new Ratings();
        ratings.setId(1L);
        ratings.setBook(book);
        ratings.setRating(3.4);

        Authors authors = new Authors();
        authors.setId(1L);
        authors.setBook(book);
        authors.setName("Test author");
        authors.setBirthYear(1934);
        authors.setDeathYear(1934);

        Languages languages = new Languages();
        languages.setBook(book);
        languages.setId(1L);
        languages.setLanguage("en");

        book.setReviews(new ArrayList<>(List.of(reviews)));
        book.setAuthors(new ArrayList<>(List.of(authors)));
        book.setLanguages(new ArrayList<>(List.of(languages)));
        book.setRatings(new ArrayList<>(List.of(ratings)));

        when(bookRepository.findByBookId(1L)).thenReturn(java.util.Optional.of(book));

        GenericApiResponseDto response = bookService.addBookReview(reviewRequest);

        assertTrue(response.isSuccess());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testAddBookReview_ShouldThrowValidationExceptionForInvalidRating() {
        BookReviewRequestDto reviewRequest = new BookReviewRequestDto();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(6.0);
        reviewRequest.setReview("Invalid Rating");

        assertThrows(ValidationException.class, () -> bookService.addBookReview(reviewRequest));
    }
}
