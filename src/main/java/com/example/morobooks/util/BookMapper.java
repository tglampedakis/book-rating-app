package com.example.morobooks.util;

import com.example.morobooks.dto.AuthorDto;
import com.example.morobooks.dto.BookDetailsDto;
import com.example.morobooks.dto.BookDto;
import com.example.morobooks.entity.*;

import java.util.List;

public class BookMapper {

    public static Book mapToBookEntity(BookDto book) {
        Book bookEntity = new Book();

        bookEntity.setBookId(book.getId());
        bookEntity.setTitle(book.getTitle());
        bookEntity.setDownloadCount(book.getDownloadCount());

        List<Authors> authorsEntity = book.getAuthors().stream()
                .map(author -> {
                    Authors newAuthor = new Authors();
                    newAuthor.setName(author.getName());
                    newAuthor.setBirthYear(author.getBirthYear());
                    newAuthor.setDeathYear(author.getDeathYear());
                    newAuthor.setBook(bookEntity);
                    return newAuthor;
                }).toList();

        List<Languages> languagesEntity = book.getLanguages().stream()
                .map(language -> {
                    Languages newLanguage = new Languages();
                    newLanguage.setLanguage(language);
                    newLanguage.setBook(bookEntity);
                    return newLanguage;
                }).toList();

        bookEntity.setAuthors(authorsEntity);
        bookEntity.setLanguages(languagesEntity);

        return bookEntity;
    }

    public static BookDto mapToBookDto(Book book) {
        List<AuthorDto> authors = book.getAuthors().stream()
                .map(author -> new AuthorDto(author.getName(), author.getBirthYear(), author.getDeathYear()))
                .toList();

        return BookDto.builder()
                .id(book.getBookId())
                .title(book.getTitle())
                .authors(authors)
                .languages(book.getLanguages().stream().map(Languages::getLanguage).toList())
                .downloadCount(book.getDownloadCount())
                .reviews(book.getReviews().stream().map(Reviews::getReview).toList())
                .build();
    }

    public static BookDto mapDetailsToBookDto(BookDetailsDto detailsDto) {
        return BookDto.builder()
                .id(detailsDto.getId())
                .title(detailsDto.getTitle())
                .downloadCount(detailsDto.getDownloadCount())
                .rating(detailsDto.getRating())
                .build();
    }

}
