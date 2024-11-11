package com.example.morobooks.util;

import com.example.morobooks.dto.AuthorDto;
import com.example.morobooks.dto.BookDetailsDto;
import com.example.morobooks.dto.BookDto;
import com.example.morobooks.entity.Authors;
import com.example.morobooks.entity.Book;
import com.example.morobooks.entity.Languages;
import com.example.morobooks.entity.Reviews;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookMapperTest {

    @Test
    void testMapToBookEntity() {
        BookDto bookDto = BookDto.builder()
                .id(1L)
                .title("Test Book")
                .downloadCount(123L)
                .authors(List.of(new AuthorDto("Test author", 1920, 2000)))
                .languages(List.of("en", "fr"))
                .build();

        Book bookEntity = BookMapper.mapToBookEntity(bookDto);

        assertNotNull(bookEntity);
        assertEquals(1L, bookEntity.getBookId());
        assertEquals("Test Book", bookEntity.getTitle());
        assertEquals(123L, bookEntity.getDownloadCount());

        assertEquals(1, bookEntity.getAuthors().size());
        assertEquals("Test author", bookEntity.getAuthors().get(0).getName());
        assertEquals(1920, bookEntity.getAuthors().get(0).getBirthYear());
        assertEquals(2000, bookEntity.getAuthors().get(0).getDeathYear());

        assertEquals(2, bookEntity.getLanguages().size());
        assertEquals("en", bookEntity.getLanguages().get(0).getLanguage());
        assertEquals("fr", bookEntity.getLanguages().get(1).getLanguage());
    }

    @Test
    void testMapToBookDto() {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setDownloadCount(123L);

        Authors author = new Authors();
        author.setName("Test author");
        author.setBirthYear(1920);
        author.setDeathYear(2000);
        book.setAuthors(List.of(author));

        Languages language1 = new Languages();
        language1.setLanguage("en");
        Languages language2 = new Languages();
        language2.setLanguage("fr");
        book.setLanguages(List.of(language1, language2));

        Reviews review = new Reviews();
        review.setReview("test review");
        book.setReviews(List.of(review));

        BookDto bookDto = BookMapper.mapToBookDto(book);

        assertNotNull(bookDto);
        assertEquals(1L, bookDto.getId());
        assertEquals("Test Book", bookDto.getTitle());
        assertEquals(123L, bookDto.getDownloadCount());

        assertEquals(1, bookDto.getAuthors().size());
        assertEquals("Test author", bookDto.getAuthors().get(0).getName());

        assertEquals(2, bookDto.getLanguages().size());
        assertTrue(bookDto.getLanguages().contains("en"));
        assertTrue(bookDto.getLanguages().contains("fr"));

        assertEquals(1, bookDto.getReviews().size());
        assertEquals("test review", bookDto.getReviews().get(0));
    }

    @Test
    void testMapDetailsToBookDto() {
        BookDetailsDto detailsDto = new BookDetailsDto(
                1L, "Bestseller test Book", 123L, 4.5
        );

        BookDto bookDto = BookMapper.mapDetailsToBookDto(detailsDto);

        assertNotNull(bookDto);
        assertEquals(1L, bookDto.getId());
        assertEquals("Bestseller test Book", bookDto.getTitle());
        assertEquals(123L, bookDto.getDownloadCount());
        assertEquals(4.5, bookDto.getRating());
    }
}
