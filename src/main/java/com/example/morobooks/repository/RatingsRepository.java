package com.example.morobooks.repository;

import com.example.morobooks.entity.Book;
import com.example.morobooks.entity.Ratings;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, Long> {

    List<Ratings> findAllByBook(Book book);

    @Query(value = "SELECT strftime('%Y-%m', r.created_at / 1000, 'unixepoch') AS month, ROUND(AVG(r.rating), 1) AS averageRating " +
            "FROM Ratings r " +
            "WHERE r.book_id = :bookId " +
            "GROUP BY month ORDER BY month DESC", nativeQuery = true)
    List<Tuple> findAverageRatingPerMonth(@Param("bookId") String bookId);

}
