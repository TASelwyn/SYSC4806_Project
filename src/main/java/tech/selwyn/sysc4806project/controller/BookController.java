package tech.selwyn.sysc4806project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.selwyn.sysc4806project.service.CatalogService;
import org.springframework.web.bind.annotation.RequestParam;
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
}