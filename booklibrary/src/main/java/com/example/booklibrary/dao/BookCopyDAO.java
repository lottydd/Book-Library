package com.example.booklibrary.dao;

import com.example.booklibrary.model.BookCopy;
import org.springframework.stereotype.Repository;

@Repository

public class BookCopyDAO extends BaseDAO< BookCopy, Integer> {
    public BookCopyDAO() {
        super(BookCopy.class);
    }
}
