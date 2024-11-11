package com.example.morobooks.repository;

import com.example.morobooks.entity.Book;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByBookId(long bookId);

    @Query(value = "SELECT b.book_id AS id, b.title, b.download_count, ROUND(AVG(r.rating), 1) AS rating " +
            "FROM books b " +
            "JOIN ratings r ON b.book_id = r.book_id " +
            "GROUP BY b.book_id, b.title, b.download_count " +
            "ORDER BY rating DESC " +
            "LIMIT :threshold", nativeQuery = true)
    List<Tuple> findBestSellerBooksQuery(@Param("threshold") Integer threshold);

}
