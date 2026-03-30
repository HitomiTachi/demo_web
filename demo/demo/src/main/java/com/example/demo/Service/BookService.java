package com.example.demo.Service;

import com.example.demo.Model.Book;
import com.example.demo.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public void updateBook(int id, Book updatedBook) {
        bookRepository.findById(id).ifPresent(b -> {
            b.setTitle(updatedBook.getTitle());
            b.setAuthor(updatedBook.getAuthor());
            bookRepository.save(b);
        });
    }

    @Transactional
    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        String k = keyword.trim();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(k, k);
    }
}
