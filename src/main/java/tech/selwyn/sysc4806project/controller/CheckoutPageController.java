package tech.selwyn.sysc4806project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * simulated checkout and order confirmation pages
 */
@Controller
public class CheckoutPageController {

    @GetMapping("/checkout/{userId}")
    public String checkoutPage(@PathVariable UUID userId, Model model) {
        model.addAttribute("userId", userId);
        return "checkout/checkout";
    }

    @GetMapping("/checkout/{userId}/confirmation/{orderId}")
    public String confirmationPage(
            @PathVariable UUID userId,
            @PathVariable UUID orderId,
            Model model) {

        model.addAttribute("userId", userId);
        model.addAttribute("orderId", orderId);

        return "checkout/confirmation";
    }
}