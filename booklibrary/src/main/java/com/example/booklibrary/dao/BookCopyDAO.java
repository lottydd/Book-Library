package com.example.booklibrary.dao;

import com.example.booklibrary.model.BookCopy;

public class BookCopyDAO extends BaseDAO< BookCopy, Integer> {
    public BookCopyDAO() {
        super(BookCopy.class);
    }
}
