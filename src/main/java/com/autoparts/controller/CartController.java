package com.autoparts.controller;

import com.autoparts.entity.CartItem;
import com.autoparts.entity.User;
import com.autoparts.service.CartService;
import com.autoparts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    private User getCurrentUser(Authentication auth) {
        return userService.getUserByUsername(auth.getName());
    }

    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        List<CartItem> items = cartService.getCartItems(user);
        BigDecimal total = cartService.getCartTotal(user);
        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Authentication auth,
                            RedirectAttributes redirectAttrs) {
        try {
            User user = getCurrentUser(auth);
            cartService.addToCart(user, productId, quantity);
            redirectAttrs.addFlashAttribute("successMsg", "Item added to cart!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/products/" + productId;
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            User user = getCurrentUser(auth);
            cartService.removeFromCart(user, id);
            redirectAttrs.addFlashAttribute("successMsg", "Item removed from cart.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable Long id,
                                 @RequestParam int quantity,
                                 Authentication auth,
                                 RedirectAttributes redirectAttrs) {
        try {
            User user = getCurrentUser(auth);
            cartService.updateCartItemQuantity(user, id, quantity);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/cart";
    }
}
