package com.example.demo.Controller;

import com.example.demo.Service.CartService;
import com.example.demo.dto.CheckoutForm;
import com.example.demo.dto.CheckoutResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {

    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        if (!model.containsAttribute("checkoutForm")) {
            model.addAttribute("checkoutForm", new CheckoutForm());
        }
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String checkoutSubmit(@Valid @ModelAttribute("checkoutForm") CheckoutForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("cartItems", cartService.getItems());
            model.addAttribute("total", cartService.getTotal());
            return "order/checkout";
        }
        CheckoutResult result = cartService.checkout(form.getRecipientName(), form.getPhone(), form.getAddress());
        if (result == null) {
            return "redirect:/cart";
        }
        return "redirect:/order/success";
    }

    @GetMapping("/order/success")
    public String checkoutSuccess() {
        return "order/success";
    }
}
