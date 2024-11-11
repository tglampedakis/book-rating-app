package com.example.morobooks.util;

import com.example.morobooks.dto.AuthorDto;
import com.example.morobooks.entity.Authors;

public class AuthorMapper {

    public static AuthorDto toDto(Authors author) {
        return new AuthorDto(author.getName(), author.getBirthYear(), author.getDeathYear());
    }

}
