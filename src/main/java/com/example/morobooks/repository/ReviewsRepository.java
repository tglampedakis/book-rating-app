package com.example.morobooks.repository;

import com.example.morobooks.entity.Book;
import com.example.morobooks.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    List<Reviews> findAllByBook(Book book);

}
