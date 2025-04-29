package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }


    public int addBook(){

    }

    public int sendBooksFromStorage(){

    }

    public boolean inStock (int bookId){

    }

    public List<BookCopy> getBookCopyList(int bookId){

    }

    public String getBookDesc (int bookId){}

    public List<Book>  getAllBooksSortedByNumbersOfCopys(){}

    public List<Book>  getAllBooksSortedByPublicationYear(){}

    public List<Book>  getAllBooksThatIsNotRentedByPeriod(){}

    public List<Book>  getAllISBNCopiesFromBook(){}

    public List<Book> findBookById(){}

}
