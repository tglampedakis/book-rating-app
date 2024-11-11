package com.example.morobooks.service;

import com.example.morobooks.dto.*;
import com.example.morobooks.entity.Book;
import com.example.morobooks.entity.Languages;
import com.example.morobooks.entity.Ratings;
import com.example.morobooks.entity.Reviews;
import com.example.morobooks.exception.BookNotFoundException;
import com.example.morobooks.exception.ValidationException;
import com.example.morobooks.feign.GutendexFeignClient;
import com.example.morobooks.repository.*;
import com.example.morobooks.util.AuthorMapper;
import com.example.morobooks.util.BookMapper;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@Service
public class BookServiceImpl implements BookService {

    private final GutendexFeignClient gutendexClient;
    private final BookRepository bookRepository;
    private final RatingsRepository ratingsRepository;
    private final AuthorRepository authorRepository;
    private final LanguagesRepository languagesRepository;
    private final ReviewsRepository reviewsRepository;

    @Autowired
    public BookServiceImpl(GutendexFeignClient gutendexClient, BookRepository bookRepository, RatingsRepository ratingsRepository, AuthorRepository authorRepository, LanguagesRepository languagesRepository, ReviewsRepository reviewsRepository) {
        this.gutendexClient = gutendexClient;
        this.bookRepository = bookRepository;
        this.ratingsRepository = ratingsRepository;
        this.authorRepository = authorRepository;
        this.languagesRepository = languagesRepository;
        this.reviewsRepository = reviewsRepository;
    }

    @Override
    @Cacheable(value = "bookSearchResults", key = "{#authorYearStart, #authorYearEnd, #languages, #ids, #search, #page, #pageSize}")
    public BooksResponseDto searchBooks(String authorYearStart, String authorYearEnd, String languages,
                                        String ids, String search, String page, String pageSize) {

        List<BookDto> books = gutendexClient.getAllBooks(authorYearStart, authorYearEnd, languages, ids, search, page, pageSize)
                .getResults();

        if (CollectionUtils.isEmpty(books)) throw new BookNotFoundException("No books found.");

        return BooksResponseDto.builder()
                .books(books)
                .build();
    }

    @Override
    @CacheEvict(value = {"bookDetails"}, key = "#bookReviewRequest.bookId")
    public GenericApiResponseDto addBookReview(BookReviewRequestDto bookReviewRequest) {
        if (bookReviewRequest.getRating() < 0.0 || bookReviewRequest.getRating() > 5.0)
            throw new ValidationException("Provided rating value must be between 0.0 and 5.0.");

        // if a book is not yet rated from the app, retrieve from the external api and then save it
        // otherwise add the rating

        bookRepository.findByBookId(bookReviewRequest.getBookId())
                .ifPresentOrElse(
                        book -> addExistingBookReview(bookReviewRequest, book),
                        () -> addNewBookReview(bookReviewRequest)
                );

        return GenericApiResponseDto.builder()
                .success(Boolean.TRUE)
                .message("Review added successfully")
                .build();
    }

    @Transactional
    private void addExistingBookReview(BookReviewRequestDto bookReviewRequest, Book book) {
        Reviews newReview = new Reviews();
        newReview.setReview(bookReviewRequest.getReview());
        newReview.setBook(book);

        if (CollectionUtils.isEmpty(book.getReviews())) {
            book.setReviews(List.of(newReview));
        } else {
            book.getReviews().add(newReview);
        }

        Ratings newRating = new Ratings();
        newRating.setBook(book);
        newRating.setRating(bookReviewRequest.getRating());
        newRating.setCreatedAt(Instant.now().toEpochMilli());

        book.getRatings().add(newRating);

        bookRepository.save(book);
    }

    @Transactional
    private void addNewBookReview(BookReviewRequestDto bookReviewRequest) {
        GutendexBooksResponseDto response = gutendexClient.getAllBooks(null,
                null, null, String.valueOf(bookReviewRequest.getBookId()), null, null, null);

        Book book = BookMapper.mapToBookEntity(response.getResults().stream().findFirst()
                .orElseThrow(() -> new BookNotFoundException("No book with id " + bookReviewRequest.getBookId() + " found.")));

        Reviews newReview = new Reviews();
        newReview.setReview(bookReviewRequest.getReview());
        newReview.setBook(book);

        book.setReviews(List.of(newReview));

        Ratings newRating = new Ratings();
        newRating.setBook(book);
        newRating.setRating(bookReviewRequest.getRating());
        newRating.setCreatedAt(Instant.now().toEpochMilli());

        book.setRatings(List.of(newRating));

        bookRepository.save(book);
    }

    @Override
    @Cacheable(value = "bookDetails", key = "#bookId")
    public BookDto getBookDetails(String bookId) {
        Book book = bookRepository.findByBookId(Long.parseLong(bookId))
                .orElseThrow(() -> new BookNotFoundException("No reviewed book with id " + bookId + " found."));

        BookDto bookDto = BookMapper.mapToBookDto(book);
        bookDto.setRating(calculateAverageRating(book));

        return bookDto;
    }

    private double calculateAverageRating(Book book) {
        // get the ratings and round them (up to 1 decimal digit)
        List<Ratings> ratings = ratingsRepository.findAllByBook(book);

        double totalRating = ratings.stream().mapToDouble(Ratings::getRating).sum();
        double averageRating = ratings.isEmpty() ? 0.0 : totalRating / ratings.size();

        return Math.round(averageRating * 10.0) / 10.0;
    }

    @Override
    public List<BookDto> getBestSellerBooks(Integer threshold) {
        // tuple is being used because native query struggles with getting the desired pojo
        List<Tuple> tuples = bookRepository.findBestSellerBooksQuery(threshold);

        List<BookDetailsDto> topBooks = tuples.stream()
                .map(tuple -> new BookDetailsDto(
                        ((Number) tuple.get("id")).longValue(),
                        tuple.get("title", String.class),
                        ((Number) tuple.get("download_count")).longValue(),
                        ((Number) tuple.get("rating")).doubleValue())).toList();

        List<BookDto> books = topBooks.stream().map(BookMapper::mapDetailsToBookDto).toList();

        books.forEach(bookDto -> {
            Book book = bookRepository.findByBookId(bookDto.getId())
                    .orElseThrow(() -> new BookNotFoundException("No book with id " + bookDto.getId() + " found."));

            bookDto.setAuthors(authorRepository.findAllByBook(book).stream().map(AuthorMapper::toDto).toList());
            bookDto.setLanguages(languagesRepository.findAllByBook(book).stream().map(Languages::getLanguage).toList());
            bookDto.setReviews(reviewsRepository.findAllByBook(book).stream().map(Reviews::getReview).toList());
        });

        return books;
    }

    @Override
    public List<BookMonthlyAvgRatingDto> getBookAvgMonthlyRating(String id) {
        // tuple is being used because native query struggles with getting the desired pojo
        List<Tuple> ratingData = ratingsRepository.findAverageRatingPerMonth(id);

        return ratingData.stream()
                .map(tuple -> new BookMonthlyAvgRatingDto(
                        tuple.get("month", String.class),
                        ((Number) tuple.get("averageRating")).doubleValue()
                )).toList();
    }
}
