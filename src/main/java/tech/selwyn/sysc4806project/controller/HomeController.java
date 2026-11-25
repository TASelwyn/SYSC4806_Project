package tech.selwyn.sysc4806project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tech.selwyn.sysc4806project.service.CatalogService;

@Controller
public class HomeController {

    private final CatalogService catalogService;

    public HomeController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("books", catalogService.getAllBooks().stream().limit(4).toList());
        return "index";
    }
}