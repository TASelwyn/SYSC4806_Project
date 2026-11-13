package tech.selwyn.sysc4806project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.selwyn.sysc4806project.service.CatalogService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletResponse;
import tech.selwyn.sysc4806project.model.Book;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/books")
public class BookController {

    private final CatalogService catalogService;

    // Spring passes in Cameron's service automatically (constructor injection)
    public BookController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // GET /books  -> shows every book
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", catalogService.getAllBooks());
        model.addAttribute("query", "");
        return "books/list";   // Kemal's template: templates/books/list.html
    }
    // GET /books/search?q=tolkien&inStock=true&sortByPrice=true
    @GetMapping("/search")
    public String searchBooks(@RequestParam(name = "q", required = false) String query,
                              @RequestParam(defaultValue = "false") boolean inStock,
                              @RequestParam(defaultValue = "false") boolean sortByPrice,
                              Model model) {
        // Empty search box: just show all books instead of an error
        if (query == null || query.isBlank()) {
            return "redirect:/books";
        }

        String trimmed = query.trim();
        model.addAttribute("books", catalogService.searchBooks(trimmed, inStock, sortByPrice));
        model.addAttribute("query", trimmed);   // so the search box keeps the text
        model.addAttribute("inStock", inStock);
        model.addAttribute("sortByPrice", sortByPrice);
        return "books/list";   // same page as the list, just filtered
    }

    // GET /books/5  -> shows one book, or a "not found" page
    @GetMapping("/{id}")
    public String bookDetails(@PathVariable UUID id, Model model, HttpServletResponse response) {
        Optional<Book> book = catalogService.getBookById(id);

        if (book.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());   // real 404 status code
            model.addAttribute("bookId", id);
            return "error/book-not-found";
        }

        model.addAttribute("book", book.get());
        return "books/detail";
    }

    // GET /books/abc  -> "abc" isn't a number, show the same "not found" page
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleInvalidId() {
        return "error/book-not-found";
    }
}