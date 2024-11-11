package com.example.morobooks.repository;

import com.example.morobooks.entity.Book;
import com.example.morobooks.entity.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguagesRepository extends JpaRepository<Languages, Long> {

    List<Languages> findAllByBook(Book book);

}
