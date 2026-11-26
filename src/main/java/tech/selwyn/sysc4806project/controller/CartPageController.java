package tech.selwyn.sysc4806project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class CartPageController {
    @GetMapping("/cart/{userId}")
    public String showCart(@PathVariable UUID userId) {
        return "cartui/cart";
    }
}
