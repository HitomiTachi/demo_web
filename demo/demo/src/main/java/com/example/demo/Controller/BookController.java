package com.example.demo.Controller;

import com.example.demo.Model.Book;
import com.example.demo.Service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String getAllBooks(Model model, @RequestParam(required = false) String search) {
        List<Book> books;
        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(search);
            model.addAttribute("searchKeyword", search);
        } else {
            books = bookService.getAllBooks();
        }
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/add";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute Book book, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "books/add";
        }
        book.setId(null);
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("message", "Thêm sách thành công!");
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sách với ID: " + id);
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "books/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable int id, @Valid @ModelAttribute Book updatedBook, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sách với ID: " + id);
            return "redirect:/books";
        }
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        updatedBook.setId(id);
        bookService.updateBook(id, updatedBook);
        redirectAttributes.addFlashAttribute("message", "Cập nhật sách thành công!");
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sách với ID: " + id);
        } else {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Xóa sách thành công!");
        }
        return "redirect:/books";
    }
}
