package com.example.morobooks.repository;

import com.example.morobooks.entity.Authors;
import com.example.morobooks.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Authors, Long> {

    List<Authors> findAllByBook(Book book);

}
